import './globals.css';

export const metadata = {
  title: 'تقدم | تحميل التطبيق',
  description: 'Landing page لتطبيق تقدم',
};

export default function RootLayout({ children }) {
  return (
    <html lang="ar" dir="rtl">
      <body>{children}</body>
    </html>
  );
}
