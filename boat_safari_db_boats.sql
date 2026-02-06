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
-- Table structure for table `boats`
--

DROP TABLE IF EXISTS `boats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `boats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `boat_name` varchar(255) NOT NULL,
  `boat_type` varchar(255) NOT NULL,
  `capacity` int NOT NULL,
  `owner_id` bigint DEFAULT NULL,
  `registration_number` varchar(255) NOT NULL,
  `status` enum('AVAILABLE','MAINTENANCE','IN_USE','INACTIVE') DEFAULT 'AVAILABLE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `registration_number` (`registration_number`),
  KEY `owner_id` (`owner_id`),
  CONSTRAINT `boats_ibfk_1` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `boats`
--

LOCK TABLES `boats` WRITE;
/*!40000 ALTER TABLE `boats` DISABLE KEYS */;
INSERT INTO `boats` VALUES (9,'River Queen','Safari Boat',12,6,'RQ001','AVAILABLE','2025-10-16 20:57:38','2025-10-16 20:57:38'),(10,'Ocean Explorer','Speed Boat',8,6,'OE002','AVAILABLE','2025-10-16 20:57:38','2025-10-16 20:57:38'),(11,'Wildlife Watcher','Safari Boat',15,6,'WW003','AVAILABLE','2025-10-16 20:57:38','2025-10-16 20:57:38'),(12,'Sunset Cruiser','Luxury Yacht',20,13,'SC004','AVAILABLE','2025-10-16 20:57:44','2025-10-16 20:57:44'),(13,'Adventure Seeker','Fishing Boat',6,13,'AS005','AVAILABLE','2025-10-16 20:57:44','2025-10-16 20:57:44'),(14,'Blue Dolphin','Catamaran',25,13,'BD006','AVAILABLE','2025-10-16 20:57:44','2025-10-16 20:57:44'),(15,'System Safari','Safari Boat',10,NULL,'SYS007','AVAILABLE','2025-10-16 20:57:50','2025-10-16 20:57:50'),(16,'System Cruiser','Speed Boat',8,NULL,'SYS008','AVAILABLE','2025-10-16 20:57:50','2025-10-16 20:57:50');
/*!40000 ALTER TABLE `boats` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-17  4:26:14
