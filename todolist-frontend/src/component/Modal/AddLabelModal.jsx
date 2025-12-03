import React, { useState } from "react";
import { X, Info } from "lucide-react";

export default function AddLabelModal({ onClose, onAdd }) {
  const [name, setName] = useState("");
  const [color, setColor] = useState("Charcoal");
  const [favorite, setFavorite] = useState(false);

  const colors = [
    { name: "Charcoal", code: "#555555" },
    { name: "Red", code: "#dc2626" },
    { name: "Blue", code: "#2563eb" },
    { name: "Green", code: "#16a34a" },
    { name: "Purple", code: "#7e22ce" },
  ];

  const handleAdd = () => {
    if (!name.trim()) return alert("Please enter a label name.");
    onAdd({ name, color, favorite });
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white w-full max-w-sm rounded-lg shadow-lg p-6 relative">
        {/* Header */}
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <h2 className="text-lg font-semibold">Add label</h2>
            <Info size={16} className="text-gray-400" />
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600"
          >
            <X size={20} />
          </button>
        </div>

        {/* Name */}
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Name
          </label>
          <input
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Enter label name..."
            className="w-full border rounded-md px-3 py-2 text-sm focus:ring-2 focus:ring-red-500 outline-none"
            maxLength={60}
          />
          <div className="text-xs text-gray-400 text-right">
            {name.length}/60
          </div>
        </div>

        {/* Color */}
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Color
          </label>
          <select
            value={color}
            onChange={(e) => setColor(e.target.value)}
            className="w-full border rounded-md px-3 py-2 text-sm focus:ring-2 focus:ring-red-500 outline-none"
          >
            {colors.map((c) => (
              <option key={c.name} value={c.name}>
                {c.name}
              </option>
            ))}
          </select>
          <div className="flex items-center gap-2 mt-2">
            <span
              className="w-4 h-4 rounded-full border"
              style={{
                backgroundColor: colors.find((c) => c.name === color)?.code,
              }}
            ></span>
            <span className="text-xs bg-gray-800 text-white px-2 py-1 rounded-md">
              {color}
            </span>
          </div>
        </div>

        {/* Favorite toggle */}
        <div className="flex items-center gap-2 mb-4">
          <input
            id="fav"
            type="checkbox"
            checked={favorite}
            onChange={() => setFavorite(!favorite)}
            className="accent-red-500 w-4 h-4"
          />
          <label htmlFor="fav" className="text-sm text-gray-700">
            Add to favorites
          </label>
        </div>

        {/* Footer */}
        <div className="flex justify-end gap-2 mt-6 border-t pt-3">
          <button
            onClick={onClose}
            className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-md"
          >
            Cancel
          </button>
          <button
            onClick={handleAdd}
            className={`px-4 py-2 text-sm text-white rounded-md ${
              name.trim()
                ? "bg-red-500 hover:bg-red-600"
                : "bg-red-300 cursor-not-allowed"
            }`}
            disabled={!name.trim()}
          >
            Add
          </button>
        </div>
      </div>
    </div>
  );
}
