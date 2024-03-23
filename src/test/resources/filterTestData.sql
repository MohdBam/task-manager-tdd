INSERT INTO agent (id, name) VALUES (1, 'Agent001');
INSERT INTO agent (id, name) VALUES (2, 'Agent002');

INSERT INTO ticket (id, description, status, created_date, assigned_agent_id)
VALUES (1, 'Ticket001', 'NEW', '2024-01-01T09:00:00', NULL);
INSERT INTO ticket (id, description, status, created_date, assigned_agent_id)
VALUES (2, 'Ticket002', 'IN_PROGRESS', CURRENT_TIMESTAMP(), 1);
INSERT INTO ticket (id, description, status, created_date, assigned_agent_id)
VALUES (3, 'Ticket003', 'NEW', CURRENT_TIMESTAMP(), NULL);
INSERT INTO ticket (id, description, status, created_date, assigned_agent_id)
VALUES (4, 'Ticket004', 'RESOLVED', CURRENT_TIMESTAMP(), 2);
INSERT INTO ticket (id, description, status, created_date, assigned_agent_id)
VALUES (5, 'Ticket005', 'RESOLVED', '2024-06-01T09:00:00', 2);
