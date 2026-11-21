import { useState } from 'react';

export default function Admin() {
  const [notice, setNotice] = useState('');
  const [latestHotel, setLatestHotel] = useState(null);

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
    if (response.ok) {
      const hotel = await response.json();
      setLatestHotel(hotel);
      setNotice('Hotel added. Add its rooms below to make it bookable.');
      event.currentTarget.reset();
    } else setNotice('This action requires an administrator account.');
  }

  async function addRoom(event) {
    event.preventDefault();
    const token = sessionStorage.getItem('stayscoutToken');
    const values = Object.fromEntries(new FormData(event.currentTarget));
    const hotelId = values.hotelId;
    delete values.hotelId;
    values.pricePerNight = Number(values.pricePerNight);
    values.capacity = Number(values.capacity);
    const response = await fetch(`/api/admin/hotels/${hotelId}/rooms`, { method: 'POST', headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` }, body: JSON.stringify(values) });
    if (response.ok) { setNotice('Room added. Guests can now reserve it from the hotel page.'); event.currentTarget.reset(); }
    else setNotice('We could not add this room. Confirm the hotel ID and administrator access.');
  }

  return <section>
    <p className="eyebrow">Operations workspace</p>
    <div className="section-head"><div><h1>Hotel management</h1><p>Keep the inventory accurate and the guest-facing search fresh.</p></div></div>
    <div className="admin-stat-grid"><article className="stat"><strong>Search index</strong><span>Refreshes after each change</span></article><article className="stat"><strong>Access</strong><span>Administrator-only actions</span></article><article className="stat"><strong>Auditable flow</strong><span>Validated server-side</span></article></div>
    <div className="admin-grid"><div className="form-stack"><form className="auth-panel form-stack" onSubmit={addHotel}>
      <h2>Add a hotel</h2>
      <label>Hotel name<input name="name" required maxLength="140" /></label>
      <div className="form-split"><label>City<input name="city" required maxLength="100" /></label><label>Country<input name="country" required maxLength="100" /></label></div>
      <label>Guest rating<input name="rating" type="number" min="0" max="5" step="0.1" required /></label>
      <label>Description<textarea name="description" maxLength="2000" rows="4" /></label>
      <button className="button" type="submit">Publish hotel</button>
      {notice && <p className="notice" role="status">{notice}</p>}
    </form><form className="auth-panel form-stack" onSubmit={addRoom}>
      <h2>Add a room</h2><p className="meta">Create inventory after publishing a hotel.</p>
      <label>Hotel ID<input name="hotelId" type="number" min="1" defaultValue={latestHotel?.id || ''} key={latestHotel?.id || 'hotel-id'} required /></label>
      <label>Room type<input name="roomType" maxLength="100" placeholder="Deluxe king" required /></label>
      <div className="form-split"><label>Price per night<input name="pricePerNight" type="number" min="1" step="0.01" required /></label><label>Guests<input name="capacity" type="number" min="1" max="20" required /></label></div>
      <button className="button" type="submit">Add bookable room</button>
    </form></div><aside className="admin-note"><p className="eyebrow">Guardrail</p><h2>Only trusted people can publish.</h2><p>Every write request is checked by the API. This screen never decides access on its own.</p></aside></div>
  </section>;
}
