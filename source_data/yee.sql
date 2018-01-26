-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- 主機: 127.0.0.1
-- 產生時間： 2018-01-15 20:08:00
-- 伺服器版本: 10.1.28-MariaDB
-- PHP 版本： 7.1.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 資料庫： `yee`
--

-- --------------------------------------------------------

--
-- 資料表結構 `cost`
--

CREATE TABLE `cost` (
  `Cost_id` int(11) NOT NULL,
  `Cost_Thing_Name` varchar(20) CHARACTER SET utf8mb4 DEFAULT NULL,
  `Cost_Money` int(11) DEFAULT NULL,
  `Date_ID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 資料表結構 `date`
--

CREATE TABLE `date` (
  `Date_id` int(11) NOT NULL,
  `Date_time` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 資料表結構 `event`
--

CREATE TABLE `event` (
  `Event_id` int(11) NOT NULL,
  `Event_Type` varchar(20) CHARACTER SET utf8mb4 DEFAULT NULL,
  `Event_Description` varchar(80) CHARACTER SET utf8mb4 DEFAULT NULL,
  `Date_Id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 資料表結構 `fish`
--

CREATE TABLE `fish` (
  `Fish_id` varchar(20) CHARACTER SET utf8mb4 NOT NULL,
  `Fish_CName` varchar(20) CHARACTER SET utf8mb4 DEFAULT NULL,
  `Fish_Length` double DEFAULT NULL,
  `Fish_Satiation` int(11) DEFAULT NULL,
  `Fish_Age` int(11) DEFAULT NULL,
  `Fish_Lively` int(11) DEFAULT NULL,
  `Fish_status` varchar(20) CHARACTER SET utf8mb4 DEFAULT NULL,
  `Fish_Healthy` varchar(20) CHARACTER SET utf8mb4 DEFAULT NULL,
  `Date_ID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 已匯出資料表的索引
--

--
-- 資料表索引 `cost`
--
ALTER TABLE `cost`
  ADD PRIMARY KEY (`Cost_id`),
  ADD KEY `Date_ID` (`Date_ID`);

--
-- 資料表索引 `date`
--
ALTER TABLE `date`
  ADD PRIMARY KEY (`Date_id`);

--
-- 資料表索引 `event`
--
ALTER TABLE `event`
  ADD PRIMARY KEY (`Event_id`),
  ADD KEY `Date_Id` (`Date_Id`);

--
-- 資料表索引 `fish`
--
ALTER TABLE `fish`
  ADD PRIMARY KEY (`Fish_id`,`Date_ID`),
  ADD KEY `Date_ID` (`Date_ID`);

--
-- 已匯出資料表的限制(Constraint)
--

--
-- 資料表的 Constraints `cost`
--
ALTER TABLE `cost`
  ADD CONSTRAINT `cost_ibfk_1` FOREIGN KEY (`Date_ID`) REFERENCES `date` (`Date_id`);

--
-- 資料表的 Constraints `event`
--
ALTER TABLE `event`
  ADD CONSTRAINT `event_ibfk_1` FOREIGN KEY (`Date_Id`) REFERENCES `date` (`Date_id`);

--
-- 資料表的 Constraints `fish`
--
ALTER TABLE `fish`
  ADD CONSTRAINT `fish_ibfk_1` FOREIGN KEY (`Date_ID`) REFERENCES `date` (`Date_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
