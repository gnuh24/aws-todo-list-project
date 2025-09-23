import React from "react";

export default function HomePage() {
  return (
    <div className="flex flex-col min-h-screen">
      <main className="flex-grow flex flex-col items-center justify-center text-center p-8">
        <h2 className="text-3xl font-bold mb-4">Chào mừng đến với NoteApp</h2>
        <p className="text-gray-600 max-w-xl mb-6">
          Đây là ứng dụng quản lý ghi chú và danh mục, được xây dựng với kiến
          trúc Severless trên AWS. Bạn có thể đăng nhập để bắt đầu tạo và quản
          lý ghi chú của mình.
        </p>
        <button className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
          Bắt đầu ngay
        </button>
      </main>
    </div>
  );
}
