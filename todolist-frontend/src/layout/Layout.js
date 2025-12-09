import React from "react";
import { Outlet } from "react-router-dom";
import Footer from "../component/Footer/Footer";
import Header from "../component/Header/Header";

export default function Layout() {
  return (
    <div className="min-h-screen w-full bg-gradient-to-b from-[#b0d0ff] via-[#d8e7ff] to-[#e8f3ff] overflow-hidden">
      <Header></Header>
      <Outlet></Outlet>
      <Footer></Footer>
    </div>
  );
}
