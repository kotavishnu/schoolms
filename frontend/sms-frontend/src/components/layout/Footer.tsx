export default function Footer() {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="bg-white border-t mt-auto">
      <div className="container mx-auto px-4 py-6 max-w-7xl">
        <div className="flex flex-col md:flex-row justify-between items-center text-sm text-gray-600">
          <p>© {currentYear} School Management System. All rights reserved.</p>
          <p className="mt-2 md:mt-0">Version 1.0.0</p>
        </div>
      </div>
    </footer>
  );
}
