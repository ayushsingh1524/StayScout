import { useEffect, useRef, useState } from 'react';

const GOOGLE_SCRIPT = 'https://accounts.google.com/gsi/client';

async function messageFrom(response, fallback) {
  try {
    const body = await response.json();
    return body.message || fallback;
  } catch {
    return fallback;
  }
}

export default function Login() {
  const [mode, setMode] = useState('login');
  const [notice, setNotice] = useState('');
  const [googleClientId, setGoogleClientId] = useState('');
  const [working, setWorking] = useState(false);
  const googleButton = useRef(null);

  useEffect(() => {
    fetch('/api/auth/google/config')
      .then((response) => response.ok ? response.json() : null)
      .then((config) => setGoogleClientId(config?.enabled ? config.clientId : ''))
      .catch(() => setGoogleClientId(''));
  }, []);

  useEffect(() => {
    if (!googleClientId || !googleButton.current) return undefined;
    let active = true;
    const render = () => {
      if (!active || !window.google?.accounts?.id || !googleButton.current) return;
      window.google.accounts.id.initialize({ client_id: googleClientId, callback: googleSignIn });
      googleButton.current.replaceChildren();
      window.google.accounts.id.renderButton(googleButton.current, { type: 'standard', theme: 'outline', size: 'large', text: 'continue_with', width: 320 });
    };
    const script = document.querySelector(`script[src="${GOOGLE_SCRIPT}"]`);
    if (script) render();
    else {
      const element = document.createElement('script');
      element.src = GOOGLE_SCRIPT;
      element.async = true;
      element.defer = true;
      element.onload = render;
      document.head.appendChild(element);
    }
    return () => { active = false; };
  }, [googleClientId]);

  async function googleSignIn({ credential }) {
    setWorking(true);
    setNotice('');
    try {
      const response = await fetch('/api/auth/google', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ credential }) });
      if (!response.ok) throw new Error(await messageFrom(response, 'Google sign-in could not be completed.'));
      const { token } = await response.json();
      sessionStorage.setItem('stayscoutToken', token);
      setNotice('You are signed in with Google. Your session is stored only in this browser tab.');
    } catch (error) {
      setNotice(error.message || 'Google sign-in could not be completed.');
    } finally {
      setWorking(false);
    }
  }

  async function submit(event) {
    event.preventDefault();
    setNotice('');
    setWorking(true);
    const values = Object.fromEntries(new FormData(event.currentTarget));
    try {
      const response = await fetch(`/api/auth/${mode === 'login' ? 'login' : 'register'}`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(values) });
      if (!response.ok) throw new Error(await messageFrom(response, 'We could not complete that request.'));
      if (mode === 'login') {
        const { token } = await response.json();
        sessionStorage.setItem('stayscoutToken', token);
        setNotice('You are signed in. Your session is stored only in this browser tab.');
      } else {
        event.currentTarget.reset();
        setMode('login');
        setNotice('Your account is ready. Sign in to start planning.');
      }
    } catch (error) {
      setNotice(error.message || 'We could not complete that request.');
    } finally {
      setWorking(false);
    }
  }

  return <section className="auth-layout">
    <div className="auth-copy"><p className="eyebrow">Stay on your terms</p><h1>One account, every thoughtful trip.</h1><p>Save the stays that feel right, keep your booking details in one place, and pick up exactly where you left off.</p><div className="trust-row"><span>✓ Clear booking details</span><span>✓ Secure sessions</span></div></div>
    <div className="auth-panel">
      <div className="auth-tabs"><button type="button" className={mode === 'login' ? 'active' : ''} onClick={() => setMode('login')}>Sign in</button><button type="button" className={mode === 'register' ? 'active' : ''} onClick={() => setMode('register')}>Create account</button></div>
      <form className="form-stack" onSubmit={submit}>
        <label>Email<input name="email" type="email" autoComplete="email" required disabled={working} /></label>
        <label>Password<input name="password" type="password" autoComplete={mode === 'login' ? 'current-password' : 'new-password'} minLength="8" required disabled={working} /></label>
        <button className="button button-wide" type="submit" disabled={working}>{working ? 'Please wait…' : mode === 'login' ? 'Sign in securely' : 'Create my account'}</button>
      </form>
      {googleClientId && <><div className="auth-divider"><span>or</span></div><div className="google-signin" ref={googleButton} aria-label="Continue with Google" /></>}
      {notice && <p className="notice" role="status">{notice}</p>}
    </div>
  </section>;
}
