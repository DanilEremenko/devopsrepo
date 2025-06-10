import React, { useState, useEffect, useCallback } from 'react';
import axiosClient from '../api/AxiosClient';
import { Table, Typography } from 'antd';
import '../styles/MentorDashboard.css';
import ProfileViewModal from './ProfileViewModal';

const { Text } = Typography;

const ManagerDashboard = () => {
    const [lastnameFilter, setLastnameFilter] = useState('');
    const [specialists, setSpecialists] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalItems, setTotalItems] = useState(0);
    const [isLoading, setIsLoading] = useState(false);

    const [selectedUserId, setSelectedUserId] = useState(null);
    const [isProfileViewModalVisible, setIsProfileViewModalVisible] = useState(false);
    const [activeCurrentUserRole, setActiveCurrentUserRole] = useState('');

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

                if (lastnameFilter) {
                    params.lastname = lastnameFilter;
                }

                const response = await axiosClient.get('/users/', { params });
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
        [lastnameFilter]
    );

    useEffect(() => {
        fetchSpecialists(currentPage);
        axiosClient.get('/user-profiles/')
            .then((response) => {
                const data = response.data;
                setActiveCurrentUserRole(data.activeRole.roleType);
            });
    }, [fetchSpecialists, currentPage]);

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    const handleFilterChange = (e) => {
        setLastnameFilter(e.target.value);
        setCurrentPage(1);
    };

    const handleFilterSubmit = () => {
        fetchSpecialists(1);
    };

    const handleUserClick = (userId) => {
        setSelectedUserId(userId);
        setIsProfileViewModalVisible(true);
    };

    const columns = [
        {
            title: 'Специалист',
            dataIndex: 'fullname',
            key: 'fullname',
            render: (text, record) => (
                <Text
                    style={{ color: '#1677FF', cursor: 'pointer' }}
                    onClick={() => handleUserClick(record.id)}
                >
                    {text}
                </Text>
            ),
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
            <h1 className="mentor-title">Специалисты компании</h1>

            <div className="filter-container">
                <div className="filter-label">Фильтр</div>
                <input
                    className="filter-input"
                    placeholder="Фамилия"
                    value={lastnameFilter}
                    onChange={handleFilterChange}
                />
                <button className="filter-button" onClick={handleFilterSubmit}>
                    Найти
                </button>
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

            <ProfileViewModal
                visible={isProfileViewModalVisible}
                onClose={() => setIsProfileViewModalVisible(false)}
                activeRole={activeCurrentUserRole}
                userId={selectedUserId}
            />
        </div>
    );
};

export default ManagerDashboard;
