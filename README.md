# تطبيق تقدم

هذا المشروع يحتوي على تطبيق Android وقناة تحميل مباشرة بواسطة صفحة HTML بسيطة.

## 1) بناء ملف APK

قبل ربط الصفحة بالتحميل، تحتاج إلى إنشاء نسخة release من التطبيق.

```bash
./gradlew assembleRelease
```

إذا كان المشروع لا يزال يحتاج إلى Android SDK أو Gradle، اضبط البيئة أولًا:

- Java 17
- Android SDK
- Android Studio
- Gradle

الملف النهائي عادةً سيكون ضمن:

```text
app/build/outputs/apk/release/app-release.apk
```

## 2) ضع ملف APK داخل الموقع

انسخ الملف الناتج إلى مجلد المشروع التالي:

```text
docs/app-release.apk
```

بعد ذلك، ستعمل صفحة التحميل مباشرة من:

```text
https://your-username.github.io/your-repo/
```

## 3) رفع المشروع على GitHub

```bash
git add .
git commit -m "Add APK landing page"
git branch -M main
git remote add origin https://github.com/your-username/your-repo.git
git push -u origin main
```

## 4) تشغيل GitHub Pages

1. افتح مستودع GitHub
2. اذهب إلى Settings
3. اضغط على Pages
4. اختر Source: Deploy from a branch
5. اختر branch: main
6. اختر folder: /docs
7. احفظ

بعد ذلك سيتم إنشاء رابط مثل:

```text
https://your-username.github.io/your-repo/
```

## 5) رابط التحميل النهائي

سيكون الرابط الرسمي للتحميل مثل:

```text
https://your-username.github.io/your-repo/app-release.apk
```

## ملاحظات

- استخدم نسخة release فقط، وليس debug
- تأكد من توقيع التطبيق في ملف release
- اختبر الملف على الهاتف قبل مشاركة الرابط

## مثال سريع

إذا كان اسم المستخدم هو: ahmed
واسم المشروع هو: taqadom
فسيكون الرابط:

```text
https://ahmed.github.io/taqadom/
```
