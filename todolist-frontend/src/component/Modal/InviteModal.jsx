import React from "react";

export default function InviteModal({ user, onClose, onInvite }) {
  return (
    <div
      className=" fixed top-16 right-6 
    w-[480px] 
    bg-white rounded-xl shadow-2xl 
    overflow-y-auto z-50 
    animate-slideIn"
    >
      <div className="bg-white h-[40vh] rounded-xl shadow-xl p-5">
        <div className="border rounded-lg flex items-center gap-3 px-3 py-2">
          <div className="w-8 h-8 bg-gray-200 rounded-full"></div>
          <span>{user?.email}</span>
          <button className="ml-auto text-gray-500 hover:text-black">✕</button>
        </div>

        <div className="flex justify-between mt-44 items-center">
          <button className="text-gray-600 hover:underline">
            Learn about sharing
          </button>

          <div className="flex gap-3">
            <button
              onClick={onClose}
              className="px-4 py-2 bg-gray-200 rounded-lg hover:bg-gray-300"
            >
              Cancel
            </button>

            <button
              onClick={onInvite}
              className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600"
            >
              Invite
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
