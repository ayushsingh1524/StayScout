import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import './Hotel.css';

const today = new Date().toISOString().slice(0, 10);

export default function Hotel() {
  const { hotelId } = useParams();
  const navigate = useNavigate();
  const [hotel, setHotel] = useState(null);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [checkIn, setCheckIn] = useState(today);
  const [checkOut, setCheckOut] = useState('');
  const [notice, setNotice] = useState('');

  useEffect(() => {
    fetch(`/api/hotels/${hotelId}`).then((response) => response.ok ? response.json() : Promise.reject())
      .then((data) => { setHotel(data); setSelectedRoom(data.rooms[0] || null); })
      .catch(() => setNotice('This stay could not be found. Please return to explore.'));
  }, [hotelId]);

  const nights = useMemo(() => {
    if (!checkIn || !checkOut || checkOut <= checkIn) return 0;
    return Math.round((new Date(`${checkOut}T00:00:00`) - new Date(`${checkIn}T00:00:00`)) / 86400000);
  }, [checkIn, checkOut]);

  async function reserve(event) {
    event.preventDefault();
    setNotice('');
    if (!sessionStorage.getItem('stayscoutToken')) { navigate('/login'); return; }
    if (!selectedRoom || !nights) { setNotice('Choose a room and a valid check-out date to continue.'); return; }
    const response = await fetch('/api/bookings', { method: 'POST', headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${sessionStorage.getItem('stayscoutToken')}` }, body: JSON.stringify({ roomId: selectedRoom.id, checkIn, checkOut }) });
    if (response.ok) { navigate('/bookings'); return; }
    const error = await response.json().catch(() => ({}));
    setNotice(error.message || 'That room is not available for these dates. Try another option.');
  }

  if (notice && !hotel) return <div className="empty"><span>✦</span><h2>We lost this stay</h2><p>{notice}</p><Link className="button" to="/">Back to explore</Link></div>;
  if (!hotel) return <div className="empty"><span>✦</span><h2>Loading your stay</h2><p>Gathering the details and room options.</p></div>;

  return <section className="hotel-detail"><Link className="back-link" to="/">← Back to explore</Link><div className="detail-hero"><div><p className="eyebrow">A considered stay</p><h1>{hotel.name}</h1><p className="detail-subtitle">{hotel.city}, {hotel.country} · {hotel.rating} ★ guest rating</p><p>{hotel.description || 'An easy base for a thoughtful escape, with rooms selected for simple comfort.'}</p></div><div className="detail-art" aria-hidden="true"><span>{hotel.rating} ★</span></div></div><div className="detail-grid"><div><h2>Choose your room</h2><div className="room-list">{hotel.rooms.map((room) => <button className={`room-option ${selectedRoom?.id === room.id ? 'selected' : ''}`} key={room.id} onClick={() => setSelectedRoom(room)}><span><strong>{room.roomType}</strong><small>Up to {room.capacity} guests</small></span><strong>₹{room.pricePerNight}<small>/ night</small></strong></button>)}</div></div><aside className="reserve-card"><p className="eyebrow">Reserve with clarity</p><h2>{selectedRoom?.roomType || 'Select a room'}</h2><form className="form-stack" onSubmit={reserve}><div className="form-split"><label>Check in<input type="date" min={today} value={checkIn} onChange={(event) => setCheckIn(event.target.value)} required /></label><label>Check out<input type="date" min={checkIn || today} value={checkOut} onChange={(event) => setCheckOut(event.target.value)} required /></label></div>{nights > 0 && <p className="total"><span>{nights} night{nights !== 1 ? 's' : ''}</span><strong>₹{Number(selectedRoom.pricePerNight) * nights}</strong></p>}<button className="button button-wide">{sessionStorage.getItem('stayscoutToken') ? 'Confirm stay' : 'Sign in to reserve'}</button></form>{notice && <p className="notice" role="status">{notice}</p>}<p className="meta">No payment details are collected in this demo booking flow.</p></aside></div></section>;
}
