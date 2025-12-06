import React from "react";

const ReviewsSection = () => {
  return (
    <section className="relative  overflow-hidden">
      {/* Sóng nền */}
      <img
        src="https://res.cloudinary.com/imagist/image/fetch/q_auto,f_auto,c_scale,w_2624/https%3A%2F%2Fwww.todoist.com%2Fstatic%2Fhome%2Fcustomer-logos-bg%402x.png"
        alt="Wave background"
        className="w-full h-auto object-cover"
      />

      {/* Nội dung đánh giá */}
      <div className="absolute inset-0 flex flex-col md:flex-row justify-center items-center md:justify-around text-center text-gray-800 px-4 py-12">
        {/* Quote 1 */}
        <div className="max-w-xs mx-6 md:mx-0 md:border-r border-gray-300 md:pr-10">
          <p className="italic text-lg leading-relaxed mb-3">
            “Simple, direct, and incredibly powerful”
          </p>
          <img
            src="https://upload.wikimedia.org/wikipedia/commons/4/4c/The_Verge_logo.svg"
            alt="The Verge"
            className="h-6 mx-auto opacity-60 hover:opacity-100 transition"
          />
        </div>

        {/* Quote 2 */}
        <div className="max-w-xs mx-6 md:mx-0 md:border-r border-gray-300 md:px-10 mt-8 md:mt-0">
          <p className="italic text-lg leading-relaxed mb-3">
            “The best to-do list on the market”
          </p>
          <img
            src="https://upload.wikimedia.org/wikipedia/commons/3/3b/PCMag_logo.svg"
            alt="PC Mag"
            className="h-8 mx-auto opacity-60 hover:opacity-100 transition"
          />
        </div>

        {/* Quote 3 */}
        <div className="max-w-xs mx-6 md:mx-0 md:pl-10 mt-8 md:mt-0">
          <p className="italic text-lg leading-relaxed mb-3">
            “Absolutely remarkable”
          </p>
          <img
            src="https://upload.wikimedia.org/wikipedia/commons/3/3e/TechRadar_logo.svg"
            alt="TechRadar"
            className="h-6 mx-auto opacity-60 hover:opacity-100 transition"
          />
        </div>
      </div>

      {/* Hiệu ứng sparkle */}
      <span className="absolute left-10 top-8 text-yellow-400 text-2xl animate-pulse">
        ✨
      </span>
      <span className="absolute right-10 bottom-8 text-yellow-400 text-2xl animate-pulse">
        ✨
      </span>
    </section>
  );
};

export default ReviewsSection;
