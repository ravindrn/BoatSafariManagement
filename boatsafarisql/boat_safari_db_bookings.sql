-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: boat_safari_db
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `trip_id` bigint DEFAULT NULL,
  `booking_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `trip_date` timestamp NOT NULL,
  `number_of_people` int NOT NULL,
  `total_price` decimal(38,2) DEFAULT NULL,
  `status` enum('PENDING','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED') DEFAULT 'PENDING',
  `special_requests` varchar(500) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `phone_number` varchar(255) NOT NULL,
  `boat_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `trip_id` (`trip_id`),
  KEY `FKnuctcpnn1oy8g5j133cs3pftl` (`boat_id`),
  CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`trip_id`) REFERENCES `trips` (`id`),
  CONSTRAINT `FKnuctcpnn1oy8g5j133cs3pftl` FOREIGN KEY (`boat_id`) REFERENCES `boats` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
INSERT INTO `bookings` VALUES (1,2,1,'2025-10-13 09:13:51','2024-02-15 04:30:00',2,150.00,'CONFIRMED',NULL,'2025-10-13 09:13:51','2025-10-13 09:13:51',NULL,'','','','',NULL),(2,3,2,'2025-10-13 09:13:51','2024-02-20 11:30:00',4,260.00,'COMPLETED',NULL,'2025-10-13 09:13:51','2025-10-13 09:13:51',NULL,'','','','',NULL),(3,2,3,'2025-10-13 09:13:51','2024-03-01 02:30:00',1,55.00,'PENDING',NULL,'2025-10-13 09:13:51','2025-10-13 09:13:51',NULL,'','','','',NULL),(4,NULL,2,'2025-10-15 13:50:34','2025-10-15 08:20:34',1,65.00,'CANCELLED','as','2025-10-15 13:50:34','2025-10-15 17:08:34','xjfjfffyjr','bro@gmail.com','bro','Kalubowila','0123456789',NULL),(5,NULL,5,'2025-10-15 13:57:01','2025-10-15 08:27:01',3,285.00,'CANCELLED','123','2025-10-15 13:57:01','2025-10-15 17:07:55','xjfjfffyjr','bro@gmail.com','bro','Kalubowila','0123456789',NULL),(6,NULL,4,'2025-10-15 14:25:44','2025-10-15 08:55:44',1,85.00,'CANCELLED','fv f','2025-10-15 14:25:44','2025-10-15 16:37:05','xjfjfffyjr','bro@gmail.com','bro','Kalubowila','0123456789',NULL),(7,NULL,1,'2025-10-15 15:53:49','2025-10-23 23:00:00',4,NULL,'CANCELLED','a','2025-10-15 15:53:49','2025-10-15 16:33:37','xjfjfffyjr','bro@gmail.com','bro','Kalubowila','0123456789',NULL),(8,NULL,3,'2025-10-15 15:57:55','2025-10-29 23:00:00',4,220.00,'CANCELLED','123','2025-10-15 15:57:55','2025-10-15 17:12:33','xjfjfffyjr','bro@gmail.com','bro','Kalubowila','0123456789',NULL),(9,NULL,2,'2025-10-16 02:19:50','2025-10-21 23:00:00',1,65.00,'CONFIRMED','dsfsvEHWTB','2025-10-16 02:19:50','2025-10-16 02:19:50','xjfjfffyjr','durangi@gmail.com','Durangi','Kalubowila','0123456789',NULL),(10,NULL,2,'2025-10-16 02:19:50','2025-10-21 23:00:00',1,65.00,'CONFIRMED','dsfsvEHWTB','2025-10-16 02:19:50','2025-10-16 02:19:50','xjfjfffyjr','durangi@gmail.com','Durangi','Kalubowila','0123456789',NULL),(11,NULL,4,'2025-10-16 02:40:32','2025-10-21 23:00:00',6,510.00,'CANCELLED','vnzgaehivjakvmadvedfedfedvecve','2025-10-16 02:40:32','2025-10-16 02:40:44','xjfjfffyjr','durangicbasics4@gmail.com','Durangi','Kalubowila','0123456789',NULL),(12,NULL,5,'2025-10-16 02:41:26','2025-10-21 23:00:00',5,475.00,'CANCELLED','dvdvdadv','2025-10-16 02:41:26','2025-10-16 02:41:31','xjfjfffyjr','durangicbasics4@gmail.com','Durangi','Kalubowila','0123456789',NULL),(13,NULL,5,'2025-10-16 03:26:34','2025-10-20 23:00:00',4,380.00,'CANCELLED','asfHEVsef','2025-10-16 03:26:34','2025-10-16 03:26:42','xjfjfffyjr','durangicbasics4@gmail.com','Durangi','Kalubowila','0123456789',NULL),(14,NULL,1,'2025-10-16 03:27:18','2025-10-27 23:00:00',3,225.00,'CANCELLED','','2025-10-16 03:27:18','2025-10-16 03:27:25','gg','durangicbasics4@gmail.com','Durangi','Kalubowila','0123456789',NULL),(15,NULL,1,'2025-10-16 03:32:35','2025-10-22 23:00:00',3,225.00,'CANCELLED','fgxjxfhjxfbxh','2025-10-16 03:32:35','2025-10-16 03:32:43','xjfjfffyjr','durangicbasics4@gmail.com','Durangi','Kalubowila','0123456789',NULL),(16,NULL,2,'2025-10-16 03:34:11','2025-10-22 23:00:00',3,195.00,'CANCELLED',',b n','2025-10-16 03:34:11','2025-10-16 03:43:38','xjfjfffyjr','durangicbasics4@gmail.com','Durangi','Kalubowila','0123456789',NULL),(17,NULL,5,'2025-10-16 03:55:34','2025-10-22 23:00:00',2,190.00,'CONFIRMED','a','2025-10-16 03:55:34','2025-10-16 04:01:38','xjfjfffyjr','durangicbasics4@gmail.com','Durangi','Kalubowila','0123456789',NULL),(18,NULL,4,'2025-10-16 08:25:39','2025-10-28 23:00:00',3,255.00,'CONFIRMED','feiuhgwoiejodfjeugvnhifhworviuo','2025-10-16 08:25:39','2025-10-16 10:08:53','xjfjfffyjr','bro@gmail.com','bro','Kalubowila','0123456789',NULL),(19,NULL,5,'2025-10-16 08:31:27','2025-10-24 23:00:00',4,380.00,'CANCELLED','fnsdfkjfowfjiojsofhweofwoffjwdkwiefhwkfhiwdhsjnwnefiuenfnipWIENF','2025-10-16 08:31:27','2025-10-16 10:08:44','xjfjfffyjr','bro@gmail.com','bro','Kalubowila','0123456789',NULL);
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-16 22:28:25
