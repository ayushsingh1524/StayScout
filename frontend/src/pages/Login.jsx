import { useState } from 'react';

export default function Login() {
  const [mode, setMode] = useState('login');
  const [notice, setNotice] = useState('');

  async function submit(event) {
    event.preventDefault();
    setNotice('');
    const values = Object.fromEntries(new FormData(event.currentTarget));
    const response = await fetch(`/api/auth/${mode === 'login' ? 'login' : 'register'}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(values),
    });

    if (!response.ok) {
      setNotice('We could not complete that request. Check your details and try again.');
      return;
    }

    if (mode === 'login') {
      const { token } = await response.json();
      sessionStorage.setItem('stayscoutToken', token);
      setNotice('You are signed in. Your session is stored only in this browser tab.');
    } else {
      setMode('login');
      setNotice('Your account is ready. Sign in to start planning.');
    }
  }

  return <section className="auth-layout">
    <div className="auth-copy">
      <p className="eyebrow">Stay on your terms</p>
      <h1>One account, every thoughtful trip.</h1>
      <p>Save the stays that feel right, keep your booking details in one place, and pick up exactly where you left off.</p>
      <div className="trust-row"><span>✓ Clear booking details</span><span>✓ Secure sessions</span></div>
    </div>
    <div className="auth-panel">
      <div className="auth-tabs">
        <button type="button" className={mode === 'login' ? 'active' : ''} onClick={() => setMode('login')}>Sign in</button>
        <button type="button" className={mode === 'register' ? 'active' : ''} onClick={() => setMode('register')}>Create account</button>
      </div>
      <form className="form-stack" onSubmit={submit}>
        <label>Email<input name="email" type="email" autoComplete="email" required /></label>
        <label>Password<input name="password" type="password" autoComplete={mode === 'login' ? 'current-password' : 'new-password'} minLength="8" required /></label>
        <button className="button button-wide" type="submit">{mode === 'login' ? 'Sign in securely' : 'Create my account'}</button>
      </form>
      {notice && <p className="notice" role="status">{notice}</p>}
    </div>
  </section>;
}
