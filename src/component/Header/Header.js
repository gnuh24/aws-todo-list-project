import React from "react";
import { Link } from "react-router-dom";
export default function Header() {
  return (
    <header className="bg-blue-600 text-white p-4 flex justify-between items-center">
      <h1 className="text-xl font-bold">NoteApp</h1>
      <nav className="space-x-4">
        <Link to="/" className="hover:underline">
          Trang chủ
        </Link>
        <Link to="/notes" className="hover:underline">
          Ghi chú
        </Link>
        <Link to="/categories" className="hover:underline">
          Danh mục
        </Link>
        <Link to="/login" className="hover:underline">
          Đăng nhập
        </Link>
      </nav>
    </header>
  );
}
