import {Menu, Input, Empty, Checkbox, Button, message} from "antd";
import { useState, useMemo } from "react";
import {https_taskflow} from "../../service/api";
import SpinnerForSettings from "../Spinner/SpinnerForSettings";

export function DropdownMenu({ selectedLabels, personalLabels, sharedLabels: projectLabels, onAddNew, onSelect, handleAISuggestion }) {
    const [search, setSearch] = useState("");
    const [aiLoading, setAiLoading] = useState(false);
    const [aiSuggestion, setAiSuggestion] = useState(null);


    // Lọc nhãn theo search
    const filteredPersonal = useMemo(() =>
            personalLabels
                .filter(lb => lb.name.toLowerCase().includes(search.toLowerCase()))
                .filter(lb => !selectedLabels.some(sl => sl.name === lb.name)),
        [search, personalLabels, selectedLabels]
    );

    const filteredProject = useMemo(() =>
            projectLabels
                .filter(lb => lb.name.toLowerCase().includes(search.toLowerCase()))
                .filter(lb => !filteredPersonal.some(pl => pl.name === lb.name))
                .filter(lb => !selectedLabels.some(sl => sl.name === lb.name)),// loại trùng với personal
        [projectLabels, search, filteredPersonal, selectedLabels]
    );

    const isEmpty = filteredPersonal.length === 0 && filteredProject.length === 0;

    return (
        <div style={{ minWidth: 200 }}>
            <Menu>
                {/* Search input nằm gọn trong menu */}
                <Menu.Item key="search" disabled style={{ padding: "4px 8px", cursor: "default" }}>
                    <Input
                        placeholder="Search labels..."
                        size="small"
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        style={{ width: "100%" }}
                    />
                </Menu.Item>

                {/* Nút AI Suggest */}
                <Menu.Item key="ai-button" disabled style={{ cursor: "default", padding: 0 }}>
                    <Button
                        type="text"
                        style={{ width: "100%", textAlign: "left", fontWeight: "bold", color: "#722ED1" }}
                        onClick={async () => {
                            setAiLoading(true);
                            const suggestion = await handleAISuggestion();
                            setAiSuggestion(suggestion);
                            setAiLoading(false);
                        }}
                    >
                        ✨ Suggest labels with AI
                    </Button>
                </Menu.Item>


                {/* Loading AI */}
                {aiLoading && (
                    <Menu.Item key="ai-loading" disabled style={{ textAlign: "center" }}>
                        <SpinnerForSettings></SpinnerForSettings>
                    </Menu.Item>
                )}

                {/* AI Suggestions */}
                {!aiLoading && aiSuggestion && (
                    <>
                        <Menu.ItemGroup title="AI Suggestion">
                            <Menu.Item
                                key="ai-suggest"
                                disabled={selectedLabels.some(lb => lb.name.toLowerCase() === aiSuggestion.toLowerCase())} // disable nếu đã chọn
                                onClick={() => {
                                    const lowerAI = aiSuggestion.toLowerCase();

                                    // Kiểm tra tồn tại trong Personal / Project
                                    const personal = filteredPersonal.find(lb => lb.name.toLowerCase() === lowerAI);
                                    const project  = filteredProject.find(lb => lb.name.toLowerCase() === lowerAI);

                                    if (personal) {
                                        onSelect(personal.id, true);  // gán personal label
                                    } else if (project) {
                                        onSelect(project.id, false);  // gán project label
                                    } else {
                                        onAddNew(aiSuggestion, true); // tạo label mới từ AI
                                    }

                                    setSearch("");
                                    setAiSuggestion(null);
                                }}
                                style={{
                                    fontWeight: filteredPersonal.concat(filteredProject).some(lb => lb.name.toLowerCase() === aiSuggestion.toLowerCase()) ? "normal" : "bold",
                                    color: filteredPersonal.concat(filteredProject).some(lb => lb.name.toLowerCase() === aiSuggestion.toLowerCase()) ? "#555" : "#FF7875",
                                }}
                            >
                                {aiSuggestion}
                                {selectedLabels.some(lb => lb.name.toLowerCase() === aiSuggestion.toLowerCase()) && (
                                    <span style={{ fontSize: 12, opacity: 0.5, marginLeft: 4 }}>(already added)</span>
                                )}
                                {!selectedLabels.some(lb => lb.name.toLowerCase() === aiSuggestion.toLowerCase()) &&
                                    filteredPersonal.concat(filteredProject).some(lb => lb.name.toLowerCase() === aiSuggestion.toLowerCase()) && (
                                        <span style={{ fontSize: 12, opacity: 0.7, marginLeft: 4 }}>(exists)</span>
                                    )}
                            </Menu.Item>
                        </Menu.ItemGroup>

                        <Menu.Divider />
                    </>
                )}


                {/* Personal labels */}
                {filteredPersonal.length > 0 && (
                    <>
                        <Menu.ItemGroup title="Personal Labels">
                            {filteredPersonal.map(lb => (
                                <Menu.Item
                                    key={`personal-${lb.id}`}
                                    onClick={() => {
                                        onSelect(lb.id,true)
                                        setSearch("");
                                        setAiSuggestion(null);
                                    }} // click để chọn label nếu cần
                                >
                                    {lb.name}
                                </Menu.Item>
                            ))}
                        </Menu.ItemGroup>
                        <Menu.Divider />
                    </>
                )}

                {/* Project labels */}
                {filteredProject.length > 0 && (
                    <>
                        <Menu.ItemGroup title="Project Labels">
                            {filteredProject.map(lb => (
                                <Menu.Item
                                    key={`shared-${lb.id}`}
                                    onClick={() => {
                                        onSelect(lb.id,false)
                                        setSearch("");
                                        setAiSuggestion(null);
                                    }} // click để chọn label nếu cần
                                >
                                    {lb.name}
                                </Menu.Item>
                            ))}
                        </Menu.ItemGroup>
                        <Menu.Divider />
                    </>
                )}



                {/* Nếu không tìm thấy nhãn */}
                {isEmpty && (
                    <>
                        <Menu.Item disabled>
                            <Empty description="Labels not found" image={Empty.PRESENTED_IMAGE_SIMPLE} />
                        </Menu.Item>
                        <Menu.Item
                            key="add-new"
                            onClick={() => {
                                onAddNew(search)
                                setSearch("")
                            }}
                            style={{ fontWeight: "bold", color: "#FF7875" }}
                        >
                            + Create label "{search}"
                        </Menu.Item>
                    </>
                )}
            </Menu>
        </div>
    );
}
