'use client';

import { useMemo, useState } from 'react';

const features = [
  { title: 'متابعة التمارين', text: 'تتبع كل جلسة تدريبية بشكل واضح وسهل.' },
  { title: 'الخطط اليومية', text: 'خطة يومية منظمة لتخفيف التشتت والاجتهاد.' },
  { title: 'تتبع التقدم', text: 'شاهد التقدم عبر المؤشرات والتقارير الأسبوعية.' },
  { title: 'واجهة عربية', text: 'تصميم عربي مريح ومناسب للمستخدمين في الخليج والعالم العربي.' },
  { title: 'تحميل سريع', text: 'بمجرد النقر، يبدأ التحميل مباشرة على الهاتف.' },
  { title: 'تثبيت سهل', text: 'تثبيت التطبيق مباشرة بعد فتح ملف APK.' },
];

export default function HomePage() {
  const [search, setSearch] = useState('');

  const filteredFeatures = useMemo(() => {
    const q = search.trim().toLowerCase();
    if (!q) return features;

    return features.filter(
      (item) =>
        item.title.toLowerCase().includes(q) || item.text.toLowerCase().includes(q)
    );
  }, [search]);

  return (
    <main className="page-shell">
      <section className="hero-card">
        <span className="badge">تطبيق تقدم</span>
        <h1>حمّل تطبيقك بسهولة على هاتفك</h1>
        <p className="subtitle">
          تطبيق عربي يساعدك على متابعة التمارين اليومية والخطط التدريبية والتقدم بشكل
          منظم وسهل.
        </p>

        <div className="search-box">
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="ابحث عن ميزة أو خدمة..."
            aria-label="بحث"
          />
        </div>

        <div className="cta-row">
          <a
            href="https://github.com/ABdullahMegahed2003/progress1/releases/download/apk-latest/taqadom.apk"
            className="primary-btn"
          >
            تحميل التطبيق
          </a>
          <a href="#features" className="secondary-btn">
            استعراض المزايا
          </a>
        </div>
      </section>

      <section id="features" className="features-wrap">
        <div className="features-header">
          <h2>المزايا الرئيسية</h2>
          <span>{filteredFeatures.length} نتيجة</span>
        </div>

        <div className="features-grid">
          {filteredFeatures.length > 0 ? (
            filteredFeatures.map((feature) => (
              <article key={feature.title} className="feature-card">
                <h3>{feature.title}</h3>
                <p>{feature.text}</p>
              </article>
            ))
          ) : (
            <div className="empty-state">لا توجد نتائج لبحثك الحالي.</div>
          )}
        </div>
      </section>
    </main>
  );
}
