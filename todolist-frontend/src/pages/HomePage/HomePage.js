import React from "react";
import { Link } from "react-router-dom";
import { AppleFilled } from "@ant-design/icons";
import { motion } from "framer-motion";
import ReviewsSection from "../../component/Section/ReviewsSection";
import "./HomePage.css";
import { useTranslation } from "react-i18next";
import Snowfall from "../../component/Effects/Snowfall";

export default function HomePage() {
  const { t } = useTranslation();

  return (
    <div className="font-sans overflow-hidden">
      <Snowfall />
      {/* Hero Section */}
      <section className="flex flex-col md:flex-row items-center justify-between px-10 py-24 max-w-7xl mx-auto gap-12">
        {/* Text Left */}
        <motion.div
          className="md:w-1/2 space-y-6"
          initial={{ opacity: 0, x: -50 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.8 }}
        >
          <h1 className="text-6xl font-extrabold leading-tight text-gray-900 drop-shadow-sm">
            Experience <br /> Productivity.
          </h1>

          <p className="text-gray-600 text-lg leading-relaxed">
            Join over <span className="font-semibold">50+ million people</span>{" "}
            leveling up their personal and professional lives with the world’s
            #1 to-do app.
          </p>

          <motion.div
            className="flex items-center border rounded-xl px-4 py-2  shadow-sm hover:shadow-md transition"
            whileHover={{ scale: 1.02 }}
          >
            <AppleFilled className="text-2xl mr-3 text-gray-800" />
            <span className="text-gray-700 font-medium">
              374,000+ ★★★★★ reviews
            </span>
            <h1 className="ml-3">{t("hello")}</h1>
          </motion.div>

          <Link to="/register">
            <motion.button
              className="px-8 py-3 text-lg font-medium rounded-full custom-button button-glow"
              whileHover={{
                scale: 1.05,
                boxShadow: "0px 0px 12px rgba(255,182,193,0.7)",
              }}
              whileTap={{ scale: 0.95 }}
            >
              Try it for free
            </motion.button>
          </Link>
        </motion.div>

        {/* Image Right */}
        <motion.div
          className="relative max-w-4xl rounded-3xl p-[3px] bg-gradient-to-tr from-white via-[#f6f9ff] to-[#dceaff] shadow-xl"
          initial={{ opacity: 0, x: 50 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.8 }}
        >
          <div className="rounded-3xl overflow-hidden ">
            <motion.img
              src="https://res.cloudinary.com/imagist/image/fetch/q_auto,f_auto,c_scale,w_960/https%3A%2F%2Fwww.todoist.com%2Fstatic%2Fhome-teams%2Fintro%2Fwide%2Fheaderui.fr.png"
              alt="Todoist preview"
              className="w-full h-auto object-cover"
              initial={{ scale: 0.95 }}
              animate={{ scale: 1 }}
              transition={{ duration: 0.8 }}
              whileHover={{ scale: 1.02 }}
            />
          </div>
        </motion.div>
      </section>

      {/* New Modern Features Section */}
      <section className="py-20 ">
        <div className="max-w-6xl mx-auto text-center space-y-12">
          <motion.h2
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
            className="text-4xl font-bold text-gray-800"
          >
            Stay organized. Stay focused.
          </motion.h2>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 px-6">
            {["Smart Tasks", "Clean UI", "Cloud Sync"].map((title, index) => (
              <motion.div
                key={index}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.8, delay: index * 0.2 }}
                className="p-8 rounded-2xl hover:shadow-xl  shadow-md border border-pink-100 transition"
              >
                <h3 className="text-2xl font-semibold text-gray-800 mb-4">
                  {title}
                </h3>
                <p className="text-gray-600 leading-relaxed">
                  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec
                  feugiat turpis sed massa luctus.
                </p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Reviews Section */}
      <ReviewsSection />
    </div>
  );
}
