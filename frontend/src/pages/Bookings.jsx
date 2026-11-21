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

  async function cancelBooking(id) {
    const token = sessionStorage.getItem('stayscoutToken');
    const response = await fetch(`/api/bookings/${id}`, { method: 'DELETE', headers: { Authorization: `Bearer ${token}` } });
    if (response.ok) setBookings((items) => items.map((item) => item.id === id ? { ...item, status: 'CANCELLED' } : item));
    else setMessage('We could not cancel this booking. Please try again.');
  }

  return <section>
    <p className="eyebrow">Your travel desk</p>
    <div className="section-head"><div><h1>My trips</h1><p>Upcoming stays, kept simple.</p></div></div>
    {message && <div className="empty"><span>✦</span><h2>Everything in one calm place</h2><p>{message}</p></div>}
    {!message && !bookings.length && <div className="empty"><span>✦</span><h2>No trips yet</h2><p>When you find the right room, your confirmed stay will appear here.</p></div>}
    {!message && <div className="booking-list">{bookings.map((booking) => <article className="booking-card" key={booking.id}>
      <div><p className="eyebrow">Confirmed stay</p><h2>{booking.hotelName}</h2><p>{booking.city} · {booking.roomType}</p></div>
      <div><strong>{booking.checkIn} → {booking.checkOut}</strong><span>₹{booking.totalPrice}</span>{booking.status === 'CONFIRMED' && <button className="text-button" onClick={() => cancelBooking(booking.id)}>Cancel stay</button>}</div>
    </article>)}</div>}
  </section>;
}
