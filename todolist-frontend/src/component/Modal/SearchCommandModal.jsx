import React, { useState } from "react";
import { X, Search } from "lucide-react";
import { useNavigate } from "react-router-dom";

export default function SearchCommandModal({ open, onClose }) {
  const [query, setQuery] = useState("");
  const navigate = useNavigate();

  if (!open) return null;

  // ✅ Thêm path để điều hướng
  const recentlyViewed = [
    {
      icon: "#",
      label: "Getting Started 👋",
      sub: "My Projects",
      path: "/app/projects/getting-started",
    },
    { icon: "📥", label: "Inbox", path: "/app/inbox" },
    { icon: "📅", label: "Upcoming", path: "/app/upcoming" },
  ];

  const navigationItems = [
    {
      icon: "🏠",
      label: "Go to home",
      shortcut: "G then H",
      path: "/app/home",
    },
    {
      icon: "📥",
      label: "Go to Inbox",
      shortcut: "G then I",
      path: "/app/inbox",
    },
    {
      icon: "📅",
      label: "Go to Today",
      shortcut: "G then T",
      path: "/app/today",
    },
    {
      icon: "📆",
      label: "Go to Upcoming",
      shortcut: "G then U",
      path: "/app/upcoming",
    },
    {
      icon: "🏷️",
      label: "Go to Filters & Labels",
      shortcut: "G then V",
      path: "/app/filters",
    },
  ];

  return (
    <div className="fixed inset-0 bg-black/40 flex items-start justify-center pt-24 z-[100]">
      <div className="bg-white w-full max-w-lg rounded-lg shadow-xl overflow-hidden">
        {/* Search bar */}
        <div className="flex items-center gap-2 border-b px-4 py-3">
          <Search size={18} className="text-gray-500" />
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search or type a command..."
            className="flex-1 outline-none text-sm"
            autoFocus
          />
          <div className="text-[11px] text-gray-400 border rounded px-1.5 py-0.5">
            Ctrl K
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600"
          >
            <X size={18} />
          </button>
        </div>

        {/* Recently viewed */}
        <div className="px-4 py-2 text-xs text-gray-500 font-semibold">
          Recently viewed
        </div>
        <div className="max-h-[300px] overflow-y-auto">
          {recentlyViewed.map((item, i) => (
            <div
              key={i}
              onClick={() => {
                navigate(item.path);
                onClose(); // ✅ Đóng modal sau khi điều hướng
              }}
              className="flex items-center justify-between px-4 py-2 hover:bg-gray-100 cursor-pointer"
            >
              <div className="flex items-center gap-3">
                <span className="w-4 text-gray-600">{item.icon}</span>
                <span className="text-sm text-gray-700">{item.label}</span>
              </div>
              {item.sub && (
                <span className="text-xs text-gray-400">{item.sub}</span>
              )}
            </div>
          ))}

          {/* Navigation */}
          <div className="px-4 py-2 text-xs text-gray-500 font-semibold mt-2 border-t">
            Navigation
          </div>
          {navigationItems.map((item, i) => (
            <div
              key={i}
              onClick={() => {
                navigate(item.path);
                onClose(); // ✅ Đóng modal sau khi click
              }}
              className="flex items-center justify-between px-4 py-2 hover:bg-gray-100 cursor-pointer"
            >
              <div className="flex items-center gap-3">
                <span className="w-4 text-gray-600">{item.icon}</span>
                <span className="text-sm text-gray-700">{item.label}</span>
              </div>
              {item.shortcut && (
                <span className="text-xs text-gray-400">{item.shortcut}</span>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
