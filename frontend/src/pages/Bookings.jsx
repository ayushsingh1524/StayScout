import { useEffect, useState } from 'react';

export default function Bookings() {
  const [bookings, setBookings] = useState([]);
  const [message, setMessage] = useState('Sign in to view and manage your bookings.');

  useEffect(() => {
    const token = sessionStorage.getItem('stayscoutToken');
    if (!token) return;
    fetch('/api/bookings', { headers: { Authorization: `Bearer ${token}` } })
      .then((response) => response.ok ? response.json() : Promise.reject())
      .then((data) => { setBookings(data); setMessage(''); })
      .catch(() => setMessage('Your session has expired. Sign in again to see your trips.'));
  }, []);

  return <section>
    <p className="eyebrow">Your travel desk</p>
    <div className="section-head"><div><h1>My trips</h1><p>Upcoming stays, kept simple.</p></div></div>
    {message && <div className="empty"><span>✦</span><h2>Everything in one calm place</h2><p>{message}</p></div>}
    {!message && <div className="booking-list">{bookings.map((booking) => <article className="booking-card" key={booking.id}>
      <div><p className="eyebrow">Confirmed stay</p><h2>{booking.hotelName}</h2><p>{booking.city} · {booking.roomType}</p></div>
      <div><strong>{booking.checkIn} → {booking.checkOut}</strong><span>₹{booking.totalPrice}</span></div>
    </article>)}</div>}
  </section>;
}
