export default function InlineAddSection({
  newSectionName,
  setNewSectionName,
  handleAddSection,
  setIsAddingSection,
}) {
  return (
    <div className="flex flex-col gap-2 mt-2">
      <input
        type="text"
        placeholder="Name this section"
        value={newSectionName}
        onChange={(e) => setNewSectionName(e.target.value)}
        className="border rounded px-3 py-2 text-sm focus:outline-none focus:ring-1 focus:ring-red-400"
      />
      <div className="flex gap-2">
        <button
          onClick={handleAddSection}
          disabled={!newSectionName.trim()}
          className={`px-3 py-1 rounded text-white text-sm ${
            newSectionName.trim()
              ? "bg-red-500 hover:bg-red-600"
              : "bg-red-300 cursor-not-allowed"
          }`}
        >
          Add section
        </button>
        <button
          onClick={() => {
            setNewSectionName("");
            setIsAddingSection(false);
          }}
          className="text-sm text-gray-500 hover:text-gray-700"
        >
          Cancel
        </button>
      </div>
    </div>
  );
}
