import React, { useState } from "react";
import { X, Info, ArrowRight } from "lucide-react";

export default function FilterAssistModal({ onClose, onSend }) {
  const [filterRequest, setFilterRequest] = useState("");

  const handleSend = () => {
    if (!filterRequest.trim())
      return toast.error("Please describe your filter.");
    onSend(filterRequest);
    onClose();
  };

  const handleExampleClick = (text) => {
    setFilterRequest(text);
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white w-full max-w-md rounded-lg shadow-lg p-6 relative">
        {/* Header */}
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <h2 className="text-lg font-semibold">Filter Assist</h2>
            <Info size={16} className="text-gray-400" />
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600"
          >
            <X size={20} />
          </button>
        </div>

        {/* Description */}
        <p className="text-sm text-gray-600 mb-2">
          Simply describe what you're looking for and Filter Assist will create
          a Todoist filter for you.{" "}
          <a href="#" className="text-red-500 hover:underline">
            Privacy Policy
          </a>
        </p>

        {/* Examples */}
        <div className="mt-3 mb-4">
          <p className="font-medium text-sm mb-2">Filter examples</p>
          <div className="flex flex-col gap-2">
            <button
              onClick={() => handleExampleClick("All tasks for next week")}
              className="flex items-center gap-2 px-3 py-2 border border-gray-200 rounded-md hover:bg-gray-50 text-sm text-gray-700"
            >
              <ArrowRight size={14} /> All tasks for next week
            </button>
            <button
              onClick={() =>
                handleExampleClick("Tasks for today with high priority")
              }
              className="flex items-center gap-2 px-3 py-2 border border-gray-200 rounded-md hover:bg-gray-50 text-sm text-gray-700"
            >
              <ArrowRight size={14} /> Tasks for today with high priority
            </button>
          </div>
        </div>

        {/* Input */}
        <div className="mt-4">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Filter request
          </label>
          <textarea
            value={filterRequest}
            onChange={(e) => setFilterRequest(e.target.value)}
            placeholder="Describe your filter..."
            className="w-full border rounded-md px-3 py-2 text-sm focus:ring-2 focus:ring-red-500 outline-none resize-none"
            rows={3}
            maxLength={1024}
          />
          <div className="text-xs text-gray-400 text-right">
            {filterRequest.length}/1024
          </div>
        </div>

        {/* Footer */}
        <div className="flex justify-end gap-2 mt-6">
          <button
            onClick={onClose}
            className="px-4 py-2 text-sm text-gray-600 hover:bg-gray-100 rounded-md"
          >
            Cancel
          </button>
          <button
            onClick={handleSend}
            className="px-4 py-2 text-sm bg-red-500 text-white rounded-md hover:bg-red-600"
          >
            Send
          </button>
        </div>
      </div>
    </div>
  );
}
