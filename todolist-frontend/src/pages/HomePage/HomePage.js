import React from "react";
import { Button } from "antd";
import { DownOutlined } from "@ant-design/icons";
import { AppleFilled } from "@ant-design/icons";
import "./HomePage.css";
import { Link } from "react-router-dom";
import ReviewsSection from "../../component/Section/ReviewsSection";
export default function HomePage() {
  return (
    <div className="font-sans bg-white">
      {/* Header */}

      {/* Hero Section */}
      <section className="flex flex-col md:flex-row items-center justify-between px-10 py-16 max-w-7xl mx-auto">
        {/* Text Left */}
        <div className="md:w-1/2 space-y-6">
          <h1 className="text-5xl font-bold leading-tight text-gray-900">
            Finally, <br /> more clarity.
          </h1>
          <p className="text-gray-600 text-lg">
            50+ million users organize their work and personal lives with the #1
            to-do app.
          </p>

          <div className="flex items-center gap-2">
            <div className="flex items-center border rounded-lg px-3 py-1 text-gray-600">
              <AppleFilled className="text-xl mr-2" />

              <span>374,000+ ★★★★★ reviews</span>
            </div>
          </div>
          <Link to="/register">
            <button className="px-6 py-3 button-glow">Try it for free</button>
          </Link>
        </div>

        {/* Image Right */}
        <div className="relative max-w-5xl rounded-2xl p-[2px] bg-gradient-to-tr from-pink-200 via-pink-100 to-white shadow-lg">
          <div className="rounded-2xl overflow-hidden bg-white">
            <img
              src="https://res.cloudinary.com/imagist/image/fetch/q_auto,f_auto,c_scale,w_960/https%3A%2F%2Fwww.todoist.com%2Fstatic%2Fhome-teams%2Fintro%2Fwide%2Fheaderui.fr.png"
              alt="Todoist preview"
              className="w-full h-auto object-cover"
            />
          </div>
        </div>
      </section>
      <ReviewsSection></ReviewsSection>
    </div>
  );
}
