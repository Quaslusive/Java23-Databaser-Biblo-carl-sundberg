-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Värd: 127.0.0.1
-- Tid vid skapande: 15 aug 2024 kl 18:31
-- Serverversion: 10.4.32-MariaDB
-- PHP-version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Databas: `dblibrary`
--

-- --------------------------------------------------------

--
-- Tabellstruktur `books`
--

CREATE TABLE `books` (
  `id` int(255) NOT NULL,
  `title` varchar(100) NOT NULL,
  `author` varchar(100) NOT NULL,
  `media_type` enum('Book','Tidning','Video','TV-spel','annat') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumpning av Data i tabell `books`
--

INSERT INTO `books` (`id`, `title`, `author`, `media_type`) VALUES
(1, '1984', 'George Orwell\r\n', 'Book'),
(2, 'Stål Kalle', 'Guido Martina, Giovan Battista Carpi', 'Tidning'),
(3, 'The Wandering Star', 'Liam Johnson', 'Book'),
(4, 'Mysteries of the Abyss', 'Emma Walker', 'Book'),
(5, 'Echoes of the Past', 'Mason Thompson', 'Book'),
(6, 'Forgotten Realms', 'Olivia Brown', 'Book'),
(7, 'Shadows in the Mist', 'Noah Davis', 'Book'),
(8, 'Whispers of the Unknown', 'Sophia Martinez', 'Book'),
(9, 'The Silent Voyage', 'James Garcia', 'Book'),
(10, 'Voices from the Deep', 'Isabella Lee', 'Book'),
(11, 'Enchanted Woods', 'Benjamin Anderson', 'Book'),
(12, 'The Secret Keeper', 'Charlotte Taylor', 'Book'),
(13, 'Tales of the Lost City', 'William Wilson', 'Book'),
(14, 'Beyond the Horizon', 'Ava Moore', 'Book'),
(15, 'The Hidden Path', 'Lucas White', 'Book'),
(16, 'Nightfall', 'Mia Harris', 'Book'),
(17, 'The Celestial Gate', 'Elijah Lewis', 'Book'),
(18, 'Chronicles of the Forsaken', 'Amelia Young', 'Book'),
(19, 'The Phantom Chronicles', 'Henry Scott', 'Book'),
(20, 'The Eternal Flame', 'Harper Adams', 'Book'),
(21, 'Frostbound', 'Alexander Nelson', 'Book'),
(22, 'The Last Kingdom', 'Evelyn King', 'Book'),
(33, 'The Times', '', 'Tidning'),
(34, 'Science Review', '', 'Tidning'),
(35, 'Illustrerad Vetenskap', '', 'Tidning'),
(36, 'Knasen', 'Mort Walker', 'Tidning'),
(37, 'Kalle Anka & C:o', '', 'Tidning'),
(38, 'Fantomen ', '', 'Tidning'),
(39, 'Aftonbladet', '', 'Tidning'),
(40, 'Falköpings Tidning', '', 'Tidning'),
(49, 'Gudfadern', 'Francis Ford Coppola', 'Video'),
(50, 'The Dark Knight', 'Christopher Nolan\r\n', 'Video'),
(51, 'Schindler\'s List', 'Steven Spielberg', 'Video'),
(52, 'Sagan om konungens återkomst', 'Peter Jackson', 'Video'),
(53, 'Matrix', 'The Wachowski Bros', 'Video'),
(54, 'Terminator 2 - Domedagen', 'James Cameron', 'Video'),
(55, 'Parasit', 'Bong Joon Ho', 'Video'),
(56, 'Alien', 'Ridley Scott', 'Video'),
(57, 'Brazil', 'Terry Gilliam', 'Video'),
(58, 'Grand Theft Auto IV', 'Rockstar North', 'TV-spel'),
(59, 'Saints Row 2', 'Volition', 'TV-spel'),
(60, 'Outer Wilds', 'Mobius Digital', 'TV-spel'),
(61, 'Elite Dangerous', 'Frontier Developments', 'TV-spel'),
(62, 'Fallout: New Vegas', 'Obsidian Entertainment', 'TV-spel'),
(63, 'Dödahavsrullarna', 'Okänd', 'annat');

-- --------------------------------------------------------

--
-- Tabellstruktur `loans`
--

CREATE TABLE `loans` (
  `id` int(255) NOT NULL,
  `user_id` int(255) NOT NULL,
  `book_id` int(255) NOT NULL,
  `loan_date` date NOT NULL,
  `return_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Tabellstruktur `users`
--

CREATE TABLE `users` (
  `id` int(255) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumpning av Data i tabell `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `name`, `email`) VALUES
(1, 'tommy01', '1234', 'Tom Scott', 'tom.scott@hotmail.com'),
(2, '', '', 'test2', 'test2@hotmail.com'),
(3, 'admin', 'admin', 'Elsbieta Jönsson', 'blueMorpho420@outlook.com');

--
-- Index för dumpade tabeller
--

--
-- Index för tabell `books`
--
ALTER TABLE `books`
  ADD PRIMARY KEY (`id`);

--
-- Index för tabell `loans`
--
ALTER TABLE `loans`
  ADD PRIMARY KEY (`id`),
  ADD KEY `loans_ibfk_2` (`user_id`),
  ADD KEY `loans_ibfk_1` (`book_id`);

--
-- Index för tabell `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT för dumpade tabeller
--

--
-- AUTO_INCREMENT för tabell `books`
--
ALTER TABLE `books`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=64;

--
-- AUTO_INCREMENT för tabell `loans`
--
ALTER TABLE `loans`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT för tabell `users`
--
ALTER TABLE `users`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Restriktioner för dumpade tabeller
--

--
-- Restriktioner för tabell `loans`
--
ALTER TABLE `loans`
  ADD CONSTRAINT `loans_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
  ADD CONSTRAINT `loans_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
