import { useState } from 'react';

export default function Admin() {
  const [notice, setNotice] = useState('');

  async function addHotel(event) {
    event.preventDefault();
    const token = sessionStorage.getItem('stayscoutToken');
    const payload = Object.fromEntries(new FormData(event.currentTarget));
    payload.rating = Number(payload.rating);
    const response = await fetch('/api/admin/hotels', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
      body: JSON.stringify(payload),
    });
    setNotice(response.ok ? 'Hotel added and search results refreshed.' : 'This action requires an administrator account.');
    if (response.ok) event.currentTarget.reset();
  }

  return <section>
    <p className="eyebrow">Operations workspace</p>
    <div className="section-head"><div><h1>Hotel management</h1><p>Keep the inventory accurate and the guest-facing search fresh.</p></div></div>
    <div className="admin-stat-grid"><article className="stat"><strong>Search index</strong><span>Refreshes after each change</span></article><article className="stat"><strong>Access</strong><span>Administrator-only actions</span></article><article className="stat"><strong>Auditable flow</strong><span>Validated server-side</span></article></div>
    <div className="admin-grid"><form className="auth-panel form-stack" onSubmit={addHotel}>
      <h2>Add a hotel</h2>
      <label>Hotel name<input name="name" required maxLength="140" /></label>
      <div className="form-split"><label>City<input name="city" required maxLength="100" /></label><label>Country<input name="country" required maxLength="100" /></label></div>
      <label>Guest rating<input name="rating" type="number" min="0" max="5" step="0.1" required /></label>
      <label>Description<textarea name="description" maxLength="2000" rows="4" /></label>
      <button className="button" type="submit">Publish hotel</button>
      {notice && <p className="notice" role="status">{notice}</p>}
    </form><aside className="admin-note"><p className="eyebrow">Guardrail</p><h2>Only trusted people can publish.</h2><p>Every write request is checked by the API. This screen never decides access on its own.</p></aside></div>
  </section>;
}
