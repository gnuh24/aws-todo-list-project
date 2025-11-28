import {Menu, Input, Empty, Checkbox} from "antd";
import { useState, useMemo } from "react";
import {https_taskflow} from "../../service/api";

export function DropdownMenu({ selectedLabels, personalLabels, sharedLabels: projectLabels, onAddNew, onSelect }) {
    const [search, setSearch] = useState("");

    console.log(personalLabels);


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

                {/* Personal labels */}
                {filteredPersonal.length > 0 && (
                    <>
                        <Menu.ItemGroup title="Personal Labels">
                            {filteredPersonal.map(lb => (
                                <Menu.Item
                                    key={`personal-${lb.id}`}
                                    onClick={() => onSelect(lb.id,true)} // click để chọn label nếu cần
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
                                    onClick={() => onSelect(lb.id,false)} // click để chọn label nếu cần
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
