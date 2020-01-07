-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 07, 2020 at 03:01 AM
-- Server version: 10.4.6-MariaDB
-- PHP Version: 7.3.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `pos`
--

-- --------------------------------------------------------

--
-- Table structure for table `barang`
--

CREATE TABLE `barang` (
  `Kode_Barang` varchar(10) NOT NULL,
  `Nama_Barang` text NOT NULL,
  `Stok` int(11) NOT NULL,
  `Satuan` text NOT NULL,
  `Harga` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `barang`
--

INSERT INTO `barang` (`Kode_Barang`, `Nama_Barang`, `Stok`, `Satuan`, `Harga`) VALUES
('B001', 'Buku Sidu 48L', 2, 'Pack', 24000),
('B002', 'Pulpen Pilot ', 2, 'Buah', 2000),
('B012', 'Tango Cheese 600g', 2, 'Kaleng', 48000),
('B020', 'Nissin Wafers 600g', 4, 'Kaleng', 45000),
('CA001', 'Paseo 250 Sheets', 1, 'Buah', 20000);

-- --------------------------------------------------------

--
-- Table structure for table `detail_barang`
--

CREATE TABLE `detail_barang` (
  `Kode_Detail` varchar(10) NOT NULL,
  `Kode_Barang` varchar(10) NOT NULL,
  `Harga` int(11) NOT NULL,
  `Jumlah` int(11) NOT NULL,
  `Discount` int(11) NOT NULL,
  `Subtotal` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `detail_barang`
--

INSERT INTO `detail_barang` (`Kode_Detail`, `Kode_Barang`, `Harga`, `Jumlah`, `Discount`, `Subtotal`) VALUES
('DTRX001', 'CA001', 20000, 1, 0, 20000),
('DTRX002', 'CA001', 20000, 2, 1000, 39000),
('DTRX003', 'B002', 2000, 2, 0, 4000),
('DTRX003', 'CA001', 20000, 1, 500, 19500),
('DTRX004', 'B012', 48000, 2, 2500, 93500),
('DTRX004', 'B001', 24000, 1, 0, 24000),
('DTRX004', 'B002', 2000, 1, 0, 2000),
('DTRX005', 'B001', 24000, 2, 0, 48000),
('DTRX005', 'B002', 2000, 2, 0, 4000),
('DTRX006', 'B002', 2000, 2, 0, 4000),
('DTRX007', 'B012', 48000, 2, 0, 96000),
('DTRX008', 'B001', 24000, 3, 0, 72000),
('DTRX008', 'B012', 48000, 5, 0, 240000),
('DTRX009', 'B012', 48000, 1, 0, 48000),
('DTRX010', 'B012', 48000, 2, 1000, 95000),
('DTRX010', 'B002', 2000, 1, 0, 2000);

-- --------------------------------------------------------

--
-- Table structure for table `login`
--

CREATE TABLE `login` (
  `Username` varchar(10) NOT NULL,
  `Password` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `login`
--

INSERT INTO `login` (`Username`, `Password`) VALUES
('admin', 'admin');

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `Kode_Transaksi` varchar(10) NOT NULL,
  `Kode_Detail` varchar(10) NOT NULL,
  `Tanggal` date NOT NULL,
  `Jam` time NOT NULL,
  `Total` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `transaksi`
--

INSERT INTO `transaksi` (`Kode_Transaksi`, `Kode_Detail`, `Tanggal`, `Jam`, `Total`) VALUES
('TRX001', 'DTRX001', '2018-07-12', '05:04:00', 200000),
('TRX002', 'DTRX002', '2018-07-13', '09:33:19', 39000),
('TRX003', 'DTRX003', '2018-07-16', '09:20:30', 23500),
('TRX004', 'DTRX004', '2018-07-13', '09:21:37', 119500),
('TRX005', 'DTRX005', '2018-07-16', '09:22:09', 52000),
('TRX006', 'DTRX006', '2018-07-16', '09:22:32', 4000),
('TRX007', 'DTRX007', '2018-07-16', '09:22:56', 96000),
('TRX008', 'DTRX008', '2020-01-05', '22:58:24', 312000),
('TRX009', 'DTRX009', '2020-01-05', '23:12:49', 48000),
('TRX010', 'DTRX010', '2020-01-06', '11:23:27', 97000);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `barang`
--
ALTER TABLE `barang`
  ADD PRIMARY KEY (`Kode_Barang`);

--
-- Indexes for table `login`
--
ALTER TABLE `login`
  ADD PRIMARY KEY (`Username`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`Kode_Transaksi`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
