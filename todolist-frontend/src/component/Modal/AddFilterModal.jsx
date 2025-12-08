import React, { useState } from "react";
import { X, Info } from "lucide-react";
import FilterAssistModal from "./FilterAssistModal";
import { toast } from "sonner";

export default function AddFilterModal({ onClose, onAdd }) {
  const [name, setName] = useState("");
  const [query, setQuery] = useState("");
  const [color, setColor] = useState("Charcoal");
  const [favorite, setFavorite] = useState(false);
  const [showAssist, setShowAssist] = useState(false);
  const handleSubmit = () => {
    if (!name.trim()) return toast.error("Please enter a name");
    const newFilter = {
      id: Date.now(),
      name,
      query,
      color,
      favorite,
    };
    onAdd(newFilter);
    onClose();
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div
        className={`bg-white w-full max-w-md rounded-lg shadow-lg p-6 relative transition-all duration-200 ${
          showAssist ? "opacity-0 pointer-events-none" : "opacity-100"
        }`}
      >
        {/* Header */}
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <h2 className="text-lg font-semibold">Add filter</h2>
            <Info size={16} className="text-gray-400" />
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600"
          >
            <X size={20} />
          </button>
        </div>

        {/* Filter Assist */}
        <div className="border rounded-md p-3 mb-4 flex items-center justify-between">
          <div>
            <p className=" flex font-semibold text-sm">
              {" "}
              <svg
                style={{ color: "#b8255f" }}
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                fill="none"
                viewBox="0 0 24 24"
                class="SGvPpEE"
              >
                <path
                  fill="currentColor"
                  fill-rule="evenodd"
                  d="m8.522 2.562-.196.639a2 2 0 0 1-1.084 1.235l-.24.11a.5.5 0 0 0 0 .91l.24.109A2 2 0 0 1 8.326 6.8l.196.639a.5.5 0 0 0 .956 0l.196-.639a2 2 0 0 1 1.084-1.235l.24-.11a.5.5 0 0 0 0-.91l-.24-.109a2 2 0 0 1-1.084-1.235l-.196-.639a.5.5 0 0 0-.956 0m10.548 8.106a5 5 0 0 1-2.61-2.79l-.805-2.137a.7.7 0 0 0-1.31 0l-.804 2.137a5 5 0 0 1-2.61 2.79l-1.529.695a.7.7 0 0 0 0 1.275l1.528.694a5 5 0 0 1 2.61 2.791l.805 2.136a.7.7 0 0 0 1.31 0l.804-2.136a5 5 0 0 1 2.61-2.79l1.529-.695a.7.7 0 0 0 0-1.275zm-13.01 5.4.485-1.066a.5.5 0 0 1 .91 0l.485 1.066a2 2 0 0 0 .993.993l1.066.484a.5.5 0 0 1 0 .91l-1.066.485a2 2 0 0 0-.993.993L7.455 21a.5.5 0 0 1-.91 0l-.485-1.066a2 2 0 0 0-.993-.993l-1.066-.485a.5.5 0 0 1 0-.91l1.066-.484a2 2 0 0 0 .993-.993"
                  clip-rule="evenodd"
                ></path>
              </svg>{" "}
              Filter Assist
            </p>

            <p style={{ width: "70%" }} className="text-xs text-gray-500">
              Describe the filter you want and we’ll generate the query for you.
            </p>
          </div>
          <button
            onClick={() => setShowAssist(true)}
            style={{ width: "68px" }}
            className="bg-red-500 hover:bg-red-600 text-white px-3 py-1 text-sm rounded-md"
          >
            Try it
          </button>
        </div>

        {/* Name Input */}
        <div className="mb-3">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Name
          </label>
          <input
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="w-full border rounded-md px-3 py-2 text-sm focus:ring-2 focus:ring-red-500 outline-none"
            maxLength={60}
          />
          <div className="text-xs text-gray-400 text-right">
            {name.length}/60
          </div>
        </div>

        {/* Query Input */}
        <div className="mb-3">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Query
          </label>
          <textarea
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            className="w-full border rounded-md px-3 py-2 text-sm focus:ring-2 focus:ring-red-500 outline-none resize-none"
            rows={3}
            maxLength={1024}
          />
          <div className="text-xs text-gray-400 text-right">
            {query.length}/1024
          </div>
        </div>

        {/* Color Select */}
        <div className="mb-3">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Color
          </label>
          <select
            value={color}
            onChange={(e) => setColor(e.target.value)}
            className="w-full border rounded-md px-3 py-2 text-sm focus:ring-2 focus:ring-red-500 outline-none"
          >
            <option>Charcoal</option>
            <option>Blue</option>
            <option>Red</option>
            <option>Green</option>
            <option>Orange</option>
          </select>
        </div>

        {/* Favorite Toggle */}
        <div className="flex items-center gap-2 mb-4">
          <input
            id="favorite"
            type="checkbox"
            checked={favorite}
            onChange={(e) => setFavorite(e.target.checked)}
            className="w-4 h-4 accent-red-500"
          />
          <label htmlFor="favorite" className="text-sm text-gray-700">
            Add to favorites
          </label>
        </div>
        <label className="block text-sm font-medium text-gray-700 mb-1">
          Preview
        </label>
        {/* Footer Buttons */}
        <div className="flex justify-end gap-2 mt-4">
          <button
            onClick={onClose}
            className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-md"
          >
            Cancel
          </button>
          <button
            onClick={handleSubmit}
            className="px-4 py-2 text-sm bg-red-500 text-white rounded-md hover:bg-red-600"
          >
            Add
          </button>
        </div>
      </div>
      {/* Nested Modal */}
      {showAssist && (
        <FilterAssistModal
          onClose={() => setShowAssist(false)}
          onSend={(text) => {
            setQuery(text);
            setShowAssist(false);
          }}
        />
      )}
    </div>
  );
}
