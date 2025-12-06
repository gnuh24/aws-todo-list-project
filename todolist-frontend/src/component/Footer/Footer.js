import React from "react";
import { FaYoutube, FaLinkedin, FaInstagram, FaWeibo } from "react-icons/fa";
import { Select } from "antd";
import LanguageSwitcher from "../LanguageSwitcher/LanguageSwitcher";

const Footer = () => {
  return (
    <footer className=" border-t border-gray-200 py-10 px-6 md:px-20">
      <div className="grid grid-cols-1 md:grid-cols-4 gap-10">
        {/* Logo và mô tả */}
        <div>
          <div className="flex items-center mb-4">
            <img
              src="https://cdn-icons-png.flaticon.com/512/5968/5968705.png"
              alt="Todoist logo"
              className="w-6 h-6 mr-2"
            />
            <span className="text-lg font-semibold text-gray-800">todoist</span>
          </div>
          <p className="text-gray-600 text-sm leading-relaxed">
            20 million users organize their work and daily life with Todoist.
          </p>
        </div>

        {/* Features */}
        <div>
          <h3 className="font-semibold text-gray-800 mb-3">Features</h3>
          <ul className="space-y-2 text-gray-600 text-sm">
            <li>Features</li>
            <li>Teams</li>
            <li>Prices</li>
            <li>Compare</li>
            <li>Models</li>
          </ul>
        </div>

        {/* Resources */}
        <div>
          <h3 className="font-semibold text-gray-800 mb-3">Resources</h3>
          <ul className="space-y-2 text-gray-600 text-sm">
            <li>Applications</li>
            <li>Help Center</li>
            <li>Customer testimonials</li>
            <li>Productivity methods</li>
            <li>Integrations</li>
            <li>Channel Partners</li>
            <li>API</li>
            <li>Status</li>
          </ul>
        </div>

        {/* Doist + mạng xã hội */}
        <div>
          <h3 className="font-semibold text-gray-800 mb-3">Doist</h3>
          <ul className="space-y-2 text-gray-600 text-sm">
            <li>About</li>
            <li className="flex items-center gap-2">
              Jobs{" "}
              <span className="bg-green-100 text-green-700 text-xs px-2 py-[2px] rounded-md">
                We're hiring!
              </span>
            </li>
            <li>Inspiration Center</li>
            <li>Press</li>
            <li>Twist</li>
          </ul>

          <div className="flex space-x-4 mt-4 text-gray-700">
            <FaYoutube className="cursor-pointer hover:text-red-600" />
            <FaLinkedin className="cursor-pointer hover:text-blue-600" />
            <FaInstagram className="cursor-pointer hover:text-pink-500" />
            <FaWeibo className="cursor-pointer hover:text-red-500" />
          </div>
        </div>
      </div>

      {/* Dòng cuối */}
      <div className="border-t border-gray-200 mt-10 pt-6 flex flex-col md:flex-row justify-between items-center text-sm text-gray-500">
        <div className="flex flex-wrap gap-4 mb-4 md:mb-0">
          <span>Security</span>
          <span>Confidentiality</span>
          <span>Terms of Use</span>
          <span>Cookie preferences</span>
          <span>© Doist Inc.</span>
        </div>

        {/* <Select
          defaultValue="French"
          options={[
            { value: "French", label: "French" },
            { value: "English", label: "English" },
          ]}
          className="w-32"
        /> */}
        <LanguageSwitcher></LanguageSwitcher>
      </div>
    </footer>
  );
};

export default Footer;
