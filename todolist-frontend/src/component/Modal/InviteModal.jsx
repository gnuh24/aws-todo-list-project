import React, { useState } from "react";
import { useParams } from "react-router-dom";
import { https_taskflow } from "../../service/api";
import { toast } from "sonner";

export default function InviteModal({ user, onClose, onInviteSuccess,setOpenSettings  }) {
  const { projectId } = useParams();
  const [loading, setLoading] = useState(false);

  const handleInvite = async () => {
    if (!user?.id) return;
console.log(user.id)
    try {
      setLoading(true);

     const res = await https_taskflow.post(
  `/v1/projects/${projectId}/members`,
  {
    idAccount: user.id,
    role: "MEMBER"
  },
  {
    headers: {
      "Content-Type": "application/json",
    }
  }
);


      toast.success("Mời member thành công!")
setOpenSettings(true)
      if (onInviteSuccess) onInviteSuccess(user);
      onClose();
    } catch (err) {
      console.error("Invite failed:", err);
      alert("Failed to invite user!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed top-16 right-6 w-[480px] bg-white rounded-xl shadow-2xl overflow-y-auto z-50 animate-slideIn">
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
              onClick={handleInvite}
              className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600"
              disabled={loading}
            >
              {loading ? "Inviting..." : "Invite"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
