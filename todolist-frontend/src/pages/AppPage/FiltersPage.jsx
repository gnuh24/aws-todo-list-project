import React, { useState } from "react";
import { Heart, Pencil, MoreHorizontal, Plus } from "lucide-react";

import MainLayout from "../../layout/MainLayout";
import EditFilterModal from "../../component/Modal/EditFilterModal";
import FilterDropdown from "../../component/Dropdown/FilterDropdown";
import AddFilterModal from "../../component/Modal/AddFilterModal";
import AddLabelModal from "../../component/Modal/AddLabelModal";

export default function FiltersPage() {
  const [filters, setFilters] = useState([
    {
      id: 1,
      name: "Assigned to me",
      query: "assigned to: me",
      color: "Charcoal",
    },
    { id: 2, name: "Priority 1", query: "priority 1", color: "Charcoal" },
  ]);
  const [openDropdownId, setOpenDropdownId] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [editingFilter, setEditingFilter] = useState(null);
  const [isAddOpen, setIsAddOpen] = useState(false);
  const handleAddLabel = (label) => {
    console.log("Label added:", label);
  };
  const handleAdd = (newFilter) => {
    setFilters((prev) => [...prev, newFilter]);
  };

  const handleDelete = (id) => {
    setFilters((prev) => prev.filter((f) => f.id !== id));
  };
  const handleSave = (updatedFilter) => {
    setFilters((prev) =>
      prev.map((f) => (f.id === updatedFilter.id ? updatedFilter : f))
    );
  };

  return (
    <MainLayout>
      <div className="p-8 bg-white min-h-screen">
        <h1 className="text-3xl font-bold mb-6">Filters & Labels</h1>

        {/* Filters */}
        <section className="mb-8">
          <div className="flex items-center justify-between mb-2">
            <div className="flex items-center gap-2">
              <span className="font-semibold text-lg">Filters</span>
              <span className="text-xs bg-gray-200 text-gray-600 px-2 py-0.5 rounded-md">
                USED: 2/3
              </span>
            </div>
            <button
              onClick={() => setIsAddOpen(true)}
              className="text-gray-500 hover:text-gray-700"
            >
              <Plus size={16} />
            </button>
          </div>
          {/* <div className="divide-y border-t border-b">
            {filters.length === 0 ? (
              <p className="text-gray-400 text-sm p-3">
                No filters yet — click + to add one.
              </p>
            ) : (
              filters.map((f) => (
                <div key={f.id} className="py-2">
                  {f.name}
                </div>
              ))
            )}
          </div> */}
          {isAddOpen && (
            <AddFilterModal
              onClose={() => setIsAddOpen(false)}
              onAdd={handleAdd}
            />
          )}
          <div className="divide-y divide-gray-200 border-t border-b">
            {filters.length === 0 ? (
              <p className="text-gray-400 text-sm p-3">
                No filters yet — click + to add one.
              </p>
            ) : (
              filters.map((f) => (
                <div
                  key={f.id}
                  className="flex items-center justify-between py-2 hover:bg-gray-50"
                >
                  <div className="flex items-center gap-2">
                    <span className="text-gray-400">💧</span>
                    <span>{f.name}</span>
                  </div>
                  <div className="flex items-center gap-3 text-gray-400">
                    <Heart
                      size={16}
                      className="hover:text-red-500 cursor-pointer"
                    />
                    <Pencil
                      size={16}
                      className="hover:text-blue-500 cursor-pointer"
                      onClick={() => setEditingFilter(f)}
                    />
                    <div className="relative">
                      <MoreHorizontal
                        size={16}
                        className="hover:text-gray-600 cursor-pointer"
                        onClick={() =>
                          setOpenDropdownId(
                            openDropdownId === f.id ? null : f.id
                          )
                        }
                      />
                      {openDropdownId === f.id && (
                        <FilterDropdown
                          onEdit={() => {
                            setEditingFilter(f);
                            setOpenDropdownId(null);
                          }}
                          onAddAbove={() => alert("Add above")}
                          onAddBelow={() => alert("Add below")}
                          onAddFavorite={() => alert("Added to favorites")}
                          onCopyLink={() => alert("Link copied")}
                          onDelete={() => handleDelete(f.id)}
                          onClose={() => setOpenDropdownId(null)}
                        />
                      )}
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </section>

        {/* Labels */}
        <section>
          <div className="flex items-center justify-between mb-2">
            <span className="font-semibold text-lg">Labels</span>
            <button
              onClick={() => setShowModal(true)}
              className="text-gray-500 hover:text-gray-700"
            >
              <Plus size={16} />
            </button>
          </div>
          <p className="text-gray-500 text-sm">
            Your list of labels will show up here.
          </p>
        </section>

        {/* Edit Filter Modal */}
        {editingFilter && (
          <EditFilterModal
            filter={editingFilter}
            onClose={() => setEditingFilter(null)}
            onSave={handleSave}
          />
        )}
        {showModal && (
          <AddLabelModal
            onClose={() => setShowModal(false)}
            onAdd={handleAddLabel}
          />
        )}
      </div>
    </MainLayout>
  );
}
