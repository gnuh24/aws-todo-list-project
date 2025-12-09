import React, { useState } from "react";
import { X } from "lucide-react";

export default function EditFilterModal({ filter, onClose, onSave }) {
  const [name, setName] = useState(filter?.name || "");
  const [query, setQuery] = useState(filter?.query || "");
  const [color, setColor] = useState(filter?.color || "Charcoal");
  const [favorite, setFavorite] = useState(false);

  const handleSubmit = () => {
    onSave({
      ...filter,
      name,
      query,
      color,
      favorite,
    });
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
      <div className="bg-white rounded-md w-[400px] shadow-lg p-6 relative">
        <button
          onClick={onClose}
          className="absolute top-3 right-3 text-gray-400 hover:text-gray-600"
        >
          <X size={18} />
        </button>

        <h2 className="text-lg font-semibold mb-4">Edit filter</h2>

        {/* Name */}
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Name
        </label>
        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="w-full border rounded-md p-2 text-sm mb-4"
          maxLength={60}
        />

        {/* Query */}
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Query
        </label>
        <textarea
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          className="w-full border rounded-md p-2 text-sm mb-4"
          maxLength={1024}
          rows={2}
        />

        {/* Color */}
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Color
        </label>
        <select
          value={color}
          onChange={(e) => setColor(e.target.value)}
          className="w-full border rounded-md p-2 text-sm mb-4"
        >
          <option>Charcoal</option>
          <option>Red</option>
          <option>Blue</option>
          <option>Green</option>
        </select>

        {/* Add to favorites */}
        <div className="flex items-center mb-4">
          <input
            type="checkbox"
            checked={favorite}
            onChange={(e) => setFavorite(e.target.checked)}
            id="fav"
            className="mr-2"
          />

          <label htmlFor="fav" className="text-sm text-gray-700">
            Add to favorites
          </label>
        </div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Preview
        </label>
        <label
          className="w-full text-gray-500  rounded-md p-2 text-xs mb-4"
          htmlFor=""
        >
          No tasks found that apply to this filter
        </label>
        {/* Buttons */}
        <div className="flex justify-end gap-3">
          <button
            onClick={onClose}
            className="px-4 py-2 text-sm border rounded-md hover:bg-gray-50"
          >
            Cancel
          </button>
          <button
            onClick={handleSubmit}
            className="px-4 py-2 text-sm bg-red-500 text-white rounded-md hover:bg-red-600"
          >
            Save
          </button>
        </div>
      </div>
    </div>
  );
}
