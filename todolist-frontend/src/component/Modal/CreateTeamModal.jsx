import { Modal, Input, Button } from "antd";
import { useState } from "react";

export default function CreateTeamModal({ open, onCancel, onContinue }) {
  const [teamName, setTeamName] = useState("");

  const handleContinue = () => {
    if (!teamName.trim()) return;
    onContinue(teamName);
    setTeamName("");
  };

  return (
    <Modal
      open={open}
      onCancel={onCancel}
      footer={null}
      width={700}
      centered
      className="custom-modal"
    >
      <div className="flex">
        {/* Left side */}
        <div className="flex-1 p-6">
          <h2 className="text-[18px] font-semibold mb-1">Create your team</h2>
          <p className="text-[13px] text-gray-600 mb-5">
            Empower your teammates with Todoist.
          </p>

          <label className="text-[13px] font-medium mb-1 block">
            Team name
          </label>
          <Input
            placeholder="The name of your team or company"
            value={teamName}
            maxLength={120}
            onChange={(e) => setTeamName(e.target.value)}
            className="rounded-md text-[13px] h-8"
          />
          <div className="text-right text-xs text-gray-400 mt-1">
            {teamName.length}/120
          </div>

          <Button
            type="primary"
            onClick={handleContinue}
            disabled={!teamName.trim()}
            className="w-full mt-5 h-9 bg-[#f87070] border-none hover:bg-[#ff6e6e]"
          >
            Continue
          </Button>

          <p className="text-[12px] text-gray-500 mt-3 text-center">
            By creating a team, you agree to our{" "}
            <a href="#" className="text-blue-500 underline">
              Terms of Service
            </a>
            . Each team workspace in Todoist is billed separately.
          </p>
        </div>

        {/* Right side */}
        <div className="w-[45%] bg-[#fffaf6] p-6 rounded-r-lg border-l">
          <img
            src="https://d3ptyyxy2at9ui.cloudfront.net/assets/images/51cd449521ea45e9.png"
            alt=""
          />
          <h3 className="font-medium mb-4 text-[14px]">
            Team productivity, made simple
          </h3>
          <ul className="text-[13px] space-y-2 text-gray-700">
            <li>✔ A shared team workspace</li>
            <li>✔ Task and project collaboration</li>
            <li>✔ Centralized access and billing</li>
            <li>✔ Team and project activity trends</li>
            <li>✔ Shared project templates</li>
            <li>✔ All personal productivity tools</li>
          </ul>
        </div>
      </div>
    </Modal>
  );
}
