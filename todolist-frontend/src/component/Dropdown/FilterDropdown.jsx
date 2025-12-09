import React, { useEffect, useRef } from "react";
import { ArrowUp, ArrowDown, Pencil, Heart, Link, Trash2 } from "lucide-react";

export default function FilterDropdown({
  onEdit,
  onDelete,
  onAddAbove,
  onAddBelow,
  onAddFavorite,
  onCopyLink,
  onClose,
}) {
  const dropdownRef = useRef();

  // Đóng khi click ra ngoài
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        onClose();
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [onClose]);

  return (
    <div
      ref={dropdownRef}
      className="absolute right-0 mt-2 w-52 bg-white border border-gray-200 shadow-lg rounded-md p-1 z-50"
    >
      <button
        onClick={onAddAbove}
        className="w-full flex items-center gap-2 px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md"
      >
        <ArrowUp size={14} /> Add filter above
      </button>
      <button
        onClick={onAddBelow}
        className="w-full flex items-center gap-2 px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md"
      >
        <ArrowDown size={14} /> Add filter below
      </button>
      <button
        onClick={onEdit}
        className="w-full flex items-center gap-2 px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md"
      >
        <Pencil size={14} /> Edit
      </button>
      <button
        onClick={onAddFavorite}
        className="w-full flex items-center gap-2 px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md"
      >
        <Heart size={14} /> Add to favorites
      </button>
      <button
        onClick={onCopyLink}
        className="w-full flex items-center gap-2 px-3 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-md"
      >
        <Link size={14} /> Copy link to filter
      </button>
      <hr className="my-1 border-gray-200" />
      <button
        onClick={onDelete}
        className="w-full flex items-center gap-2 px-3 py-2 text-sm text-red-500 hover:bg-red-50 rounded-md"
      >
        <Trash2 size={14} /> Delete
      </button>
    </div>
  );
}
