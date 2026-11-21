import { Link } from 'react-router-dom';
import { useState } from 'react';
import './Home.css';

const fallback = [
  { id: 1, name: 'Palm House Goa', city: 'Goa', rating: 4.8 },
  { id: 2, name: 'Cedar Courtyard', city: 'Jaipur', rating: 4.6 },
  { id: 3, name: 'Harbour Light', city: 'Kochi', rating: 4.7 },
];
const defaultImage = 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1400&q=85';

export default function Home() {
  const [city, setCity] = useState('');
  const [hotels, setHotels] = useState(fallback);
  const [searched, setSearched] = useState(false);
  const [status, setStatus] = useState('');

  async function search(event) {
    event.preventDefault();
    const query = city.trim();
    setSearched(true);
    setStatus('');
    try {
      const response = await fetch(`/api/hotels?location=${encodeURIComponent(query)}&size=9`);
      if (!response.ok) throw new Error('Search unavailable');
      const results = (await response.json()).content || [];
      setHotels(results);
      setStatus(results.length ? `${results.length} stay${results.length === 1 ? '' : 's'} found${query ? ` in ${query}` : ''}.` : `No stays from our current inventory match ${query || 'that location'}. You can search by city or country; try Bengaluru, Goa, Jaipur, Kochi, Mumbai, Delhi, Hyderabad, Udaipur, Manali, Pondicherry, or India.`);
    } catch {
      setHotels([]);
      setStatus('Search is temporarily unavailable. Please try again.');
    }
  }

  return <>
    <section className="hero"><div><div className="eyebrow">Stay closer to what matters</div><h1>A better way to choose your next stay.</h1><p>Clear prices, considered spaces and a calm search experience—built for weekends away and everything after.</p><form className="search-panel" onSubmit={search}><div className="field"><label htmlFor="city">Where are you going?</label><input id="city" value={city} placeholder="City or country — Bengaluru, India…" onChange={(event) => setCity(event.target.value)} /></div><button className="button">Search stays</button></form></div><div className="hero-art" aria-label="Abstract travel illustration" /></section>
    <section><div className="section-head"><div><div className="eyebrow">Handpicked for clarity</div><h2>{searched ? 'Search results' : 'Places worth staying for'}</h2></div><span className="meta">Prices shown per night</span></div>{status && <p className="search-status" role="status">{status}</p>}<div className="filters"><button className="chip active" type="button">Top rated</button><button className="chip" type="button">Under ₹5,000</button><button className="chip" type="button">For two</button><button className="chip" type="button">Free cancellation</button></div><div className="hotel-grid">{hotels.map((hotel) => <article className="hotel-card" key={hotel.id}><div className="hotel-image"><img src={hotel.imageUrl || defaultImage} alt={`Room at ${hotel.name}`} onError={(event) => { event.currentTarget.remove(); }} /><span>{hotel.rating} ★</span></div><div className="hotel-body"><h3>{hotel.name}</h3><p className="meta">{hotel.city}, {hotel.country || 'India'} · Considered comfort</p><div className="price"><strong>Explore rooms</strong><Link className="text-link" to={`/hotels/${hotel.id}`}>View rooms →</Link></div></div></article>)}</div>{searched && !hotels.length && <div className="empty"><span>✦</span><h2>No stays found yet</h2><p>Try a different city, or explore one of our featured destinations.</p></div>}</section>
  </>;
}
