import { Outlet } from 'react-router-dom';
import Header from './Header';
import Footer from './Footer';

export default function MainLayout() {
  return (
    <div className="min-h-screen flex flex-col bg-gradient-to-br from-slate-50 via-purple-50/30 to-teal-50/30">
      <Header />
      <main className="flex-1 container mx-auto px-4 py-8 max-w-7xl">
        <Outlet />
      </main>
      <Footer />
    </div>
  );
}
