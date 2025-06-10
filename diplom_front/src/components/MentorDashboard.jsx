import React, { useState, useEffect, useCallback } from 'react';
import axiosClient from '../api/AxiosClient';
import { Table, Typography } from 'antd';
import '../styles/MentorDashboard.css';

const { Text } = Typography;

const MentorDashboard = () => {
    const [activeTab, setActiveTab] = useState('mentees');
    const [specialists, setSpecialists] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalItems, setTotalItems] = useState(0);
    const [isLoading, setIsLoading] = useState(false);

    const fetchSpecialists = useCallback(
        async (page = 1) => {
            setIsLoading(true);
            try {
                const params = {
                    role: 'SPECIALIST',
                    sortBy: 'lastname',
                    order: 'asc',
                    page,
                    count: 4,
                };

                if (activeTab === 'skillCheck') {
                    params.needsSkillCheck = true;
                } else if (activeTab === 'calibration') {
                    params.needsCalibration = true;
                } else if (activeTab === 'mentees') {
                    params.isMentee = true;
                }

                const response = await axiosClient.get('/users/', { params });
                console.log('API Response:', response.data);
                const fetchedUsers = response.data.items || [];
                const filteredSpecialists = fetchedUsers.filter((user) =>
                    user.role.includes('SPECIALIST')
                );
                setSpecialists(filteredSpecialists);
                setTotalItems(response.data.page?.totalItems || 0);
            } catch (error) {
                console.error('Ошибка при загрузке специалистов:', error);
                setSpecialists([]);
                setTotalItems(0);
            } finally {
                setIsLoading(false);
            }
        },
        [activeTab]
    );

    useEffect(() => {
        fetchSpecialists(currentPage);
    }, [fetchSpecialists, currentPage]);

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    const handleTabChange = (tab) => {
        setActiveTab(tab);
        setCurrentPage(1);
    };

    const columns = [
        {
            title: 'Специалист',
            dataIndex: 'fullname',
            key: 'fullname',
            render: (text) => <Text style={{ color: '#1677FF' }}>{text}</Text>,
            width: '33.33%',
        },
        {
            title: 'Дата регистрации',
            dataIndex: 'registrationDate',
            key: 'registrationDate',
            width: '33.33%',
        },
        {
            title: 'Дата обновления',
            dataIndex: 'updateDate',
            key: 'updateDate',
            width: '33.33%',
        },
    ];

    return (
        <div className="mentor-content">
            <h1 className="mentor-title">Специалисты на проверку и калибровку навыков</h1>

            <div className="mentor-tabs">
        <span
            className={`tab-item ${activeTab === 'skillCheck' ? 'active' : 'inactive'}`}
            onClick={() => handleTabChange('skillCheck')}
        >
          На проверку навыков
        </span>
                <span
                    className={`tab-item ${activeTab === 'calibration' ? 'active' : 'inactive'}`}
                    onClick={() => handleTabChange('calibration')}
                >
          На калибровку
        </span>
                <span
                    className={`tab-item ${activeTab === 'mentees' ? 'active' : 'inactive'}`}
                    onClick={() => handleTabChange('mentees')}
                >
          Мои подопечные
        </span>
            </div>

            <div className="mentor-table-container">
                <Table
                    className="mentor-table"
                    columns={columns}
                    dataSource={specialists}
                    rowKey="id"
                    pagination={{
                        current: currentPage,
                        total: totalItems,
                        pageSize: 4,
                        onChange: handlePageChange,
                        showSizeChanger: false,
                    }}
                    loading={isLoading}
                    bordered={false}
                    scroll={{ x: 980 }}
                />
            </div>
        </div>
    );
};

export default MentorDashboard;