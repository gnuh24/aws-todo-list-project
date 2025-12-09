import React from "react";
import { useTranslation } from "react-i18next";
import { Select } from "antd";

export default function LanguageSwitcher() {
  const { i18n } = useTranslation();

  const handleChange = (value) => {
    // value là 'vi' hoặc 'en'
    i18n.changeLanguage(value);
  };

  return (
    <div className="flex gap-2">
      <Select
        defaultValue={i18n.language} // giá trị mặc định là ngôn ngữ hiện tại
        onChange={handleChange}
        options={[
          { value: "vi", label: "🇻🇳 Vietnamese" },
          { value: "en", label: "🇺🇸 English" },
        ]}
        className="w-32"
      />
    </div>
  );
}
