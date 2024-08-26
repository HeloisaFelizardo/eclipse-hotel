// Quantos clientes temos na base?
SELECT COUNT(*) AS total_clients
FROM tb_customers;

// Quantos quartos temos cadastrados?
SELECT COUNT(*) AS total_rooms
FROM tb_rooms;

// Quantas reservas em aberto o hotel possui no momento?
SELECT COUNT(*) AS open_reservations
FROM tb_reservations
WHERE status IN ('SCHEDULED', 'IN_USE');

// Quantos quartos temos vagos no momento?
SELECT COUNT(*) AS vacant_rooms
FROM tb_rooms
WHERE room_id NOT IN (
    SELECT room_id
    FROM tb_reservations
    WHERE status IN ('SCHEDULED', 'IN_USE')
);

// Quantos quartos temos ocupados no momento?
SELECT COUNT(*) AS occupied_rooms
FROM tb_reservations
WHERE status = 'IN_USE';

// Quantas reservas futuras o hotel possui?
SELECT COUNT(*) AS future_reservations
FROM tb_reservations
WHERE status = 'SCHEDULED' ;

// Qual o quarto mais caro do hotel?
SELECT type, price
FROM tb_rooms
ORDER BY price DESC
    LIMIT 1;

// Qual o quarto com maior histórico de cancelamentos?
SELECT room_id, COUNT(*) AS cancellation_count
FROM tb_reservations
WHERE status = 'CANCELED'
GROUP BY room_id
ORDER BY cancellation_count DESC
    LIMIT 1;

// Liste todos os quartos e a quantidade de clientes que já ocuparam cada um
SELECT r.room_id, COUNT(DISTINCT res.customer_id) AS customer_count
FROM tb_rooms r
LEFT JOIN tb_reservations res ON r.room_id = res.room_id
WHERE   res.status IN ('SCHEDULED', 'IN_USE', 'FINISHED')
GROUP BY r.room_id;

// Quais são os 3 quartos que possuem um histórico maior de ocupações?
SELECT room_id, COUNT(*) AS occupation_count
FROM tb_reservations
WHERE status IN ('IN_USE', 'FINISHED')
GROUP BY room_id
ORDER BY occupation_count DESC
    LIMIT 3;

/* No próximo mês, o hotel fará uma promoção para os seus 10 clientes que possuírem maior histórico de reservas
  e você foi acionado pelo seu time para extrair esta informação do banco de dados. Quem são os 10 clientes?
*/
SELECT c.customer_id, c.name, COUNT(*) AS reservation_count
FROM tb_customers c
JOIN tb_reservations r ON c.customer_id = r.customer_id
WHERE r.status IN ('IN_USE', 'FINISHED')
GROUP BY c.customer_id, c.name
ORDER BY reservation_count DESC
    LIMIT 10;

