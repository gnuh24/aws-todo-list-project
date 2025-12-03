import React, { useState } from "react";
import ShareModal from "../Modal/ShareModal";
import InviteModal from "../Modal/InviteModal";
import ShareSettingsModal from "../Modal/ShareSettingsModal";
import { Share2, LayoutList, MessageSquare } from "lucide-react";

export default function ProjectHeader() {
  const [openShare, setOpenShare] = useState(false);
  const [openInvite, setOpenInvite] = useState(false);
  const [openSettings, setOpenSettings] = useState(false);

  const [selectedEmail, setSelectedEmail] = useState("");

  const handleSelectUser = (email) => {
    setSelectedEmail(email);
    setOpenShare(false);
    setOpenInvite(true); // mở modal invite
  };

  const handleInviteDone = () => {
    setOpenInvite(false);
    setOpenSettings(true); // mở share settings
  };
  const handleClickShare = () => {
    setOpenShare(true);
    setOpenSettings(false);
    setOpenInvite(false);
  };
  return (
    <>
      <header className="flex justify-between items-center px-6 py-3 border-b">
        <div className="flex items-center gap-2 text-sm text-gray-600">
          <span className="cursor-pointer hover:underline">My Projects</span>
          <span>/</span>
        </div>

        <div className="flex items-center gap-5 text-gray-700">
          <button
            onClick={() => handleClickShare()}
            className="flex items-center gap-1 hover:text-black"
          >
            <Share2 size={16} />
            <span>Share</span>
          </button>

          <button className="flex items-center gap-1 hover:text-black">
            <LayoutList size={16} />
            <span>Display</span>
          </button>

          <button className="flex items-center gap-1 hover:text-black">
            <MessageSquare size={16} />
            <span>1</span>
          </button>

          <button className="hover:text-black">⋯</button>
        </div>
      </header>

      {/* SHARE MODAL */}
      {openShare && (
        <ShareModal
          onClose={() => setOpenShare(false)}
          onSelectUser={handleSelectUser}
        />
      )}

      {/* INVITE MODAL */}
      {openInvite && (
        <InviteModal
          user={{ email: selectedEmail }}
          onClose={() => setOpenInvite(false)}
          onInvite={handleInviteDone}
        />
      )}

      {/* SHARE SETTINGS MODAL */}
      {openSettings && (
        <ShareSettingsModal
          onClose={() => setOpenSettings(false)}
          invitedEmail={selectedEmail}
        />
      )}
    </>
  );
}
