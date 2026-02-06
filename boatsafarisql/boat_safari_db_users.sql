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
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` varchar(50) NOT NULL DEFAULT 'CUSTOMER',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `last_login` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin@boatsafari.com','admin123','System','Admin',NULL,'SYSTEM_ADMINISTRATOR','2025-10-13 09:13:51','2025-10-15 12:36:47',NULL,1,'2025-10-15 07:49:12.697867'),(2,'john_doe','john@example.com','password123','John','Doe',NULL,'CUSTOMER','2025-10-13 09:13:51','2025-10-14 20:21:38',NULL,1,NULL),(3,'jane_smith','jane@example.com','password123','Jane','Smith',NULL,'CUSTOMER','2025-10-13 09:13:51','2025-10-14 20:21:38',NULL,1,NULL),(4,'customer','customer@boatsafari.com','customer123','John','Doe',NULL,'CUSTOMER','2025-10-14 13:05:40','2025-10-15 07:56:34',NULL,0,'2025-10-15 07:53:35.290714'),(5,'staff','staff@boatsafari.com','staff123','Jane','Smith',NULL,'STAFF','2025-10-14 13:05:40','2025-10-15 10:02:58',NULL,1,NULL),(6,'owner','owner@boatsafari.com','owner123','Mike','Johnson',NULL,'BOAT_OWNER','2025-10-14 13:15:12','2025-10-14 15:48:51',NULL,1,'2025-10-14 21:18:51.098784'),(7,'manager','manager@boatsafari.com','manager123','Sarah','Wilson',NULL,'OPERATIONS_MANAGER','2025-10-14 13:15:12','2025-10-14 20:21:38',NULL,1,NULL),(8,'testmanual','testmanual@example.com','test123','Test','Manual','+94123456789','CUSTOMER','2025-10-14 20:23:08','2025-10-14 20:23:08','SYSTEM',1,NULL),(9,'bro','bro@gmail.com','123456','bro','','+9412312312','CUSTOMER','2025-10-14 14:59:57','2025-10-15 01:56:01','SYSTEM',1,'2025-10-15 07:26:00.752257'),(10,'durangicbasics4','durangicbasics4@gmail.com','Durangi123','Durangi','Kalubowila','0741394393','CUSTOMER','2025-10-14 15:03:02','2025-10-14 15:40:01','SYSTEM',1,'2025-10-14 21:10:00.543556'),(11,'Duraaa','durangicbasics@gmail.com','123456','Durangi','Kalubowila','0123456789','STAFF','2025-10-15 01:27:56','2025-10-15 01:27:56','admin',1,NULL),(12,'Duraaa123','nimasha@gmail.com','123456','Durangi','Kalubowila','0123456789','OPERATIONS_MANAGER','2025-10-15 07:12:07','2025-10-15 07:12:07','admin',1,NULL),(13,'bossbuwa','buwa@gmail.com','123456','buwa','boss','0123456789','BOAT_OWNER','2025-10-15 09:09:32','2025-10-15 09:09:32','admin',1,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-16 22:28:24
