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
-- Table structure for table `trips`
--

DROP TABLE IF EXISTS `trips`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trips` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `duration` int NOT NULL,
  `capacity` int NOT NULL,
  `price_per_person` decimal(38,2) NOT NULL,
  `destination` varchar(255) NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `featured` bit(1) DEFAULT b'0',
  `active` bit(1) DEFAULT b'1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trips`
--

LOCK TABLES `trips` WRITE;
/*!40000 ALTER TABLE `trips` DISABLE KEYS */;
INSERT INTO `trips` VALUES (1,'Amazon Wildlife Safari','Wildlife Safari','Explore the diverse wildlife of the Amazon River with our expert guides.',4,25,75.00,'Amazon River','/uploads/trips/e9ae4e43-0c73-4c82-bedd-b24d730e20f4.jpg',_binary '\0',_binary '','2025-10-13 09:13:51','2025-10-13 14:46:19'),(2,'Nile Sunset Cruise','Sunset Cruise','Enjoy a breathtaking sunset while cruising along the historic Nile River.',2,30,65.00,'Nile River','/uploads/trips/06ed7ba1-e612-424b-ac44-c08276d5417b.PNG',_binary '',_binary '','2025-10-13 09:13:51','2025-10-13 14:22:20'),(3,'Mississippi Bird Watching','Bird Watching','Perfect for bird enthusiasts! Spot various species in their natural habitat.',4,20,55.00,'Mississippi River','https://images.unsplash.com/photo-1501426026826-31c667bdf23d?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80',_binary '',_binary '','2025-10-13 09:13:51','2025-10-13 09:13:51'),(4,'Mekong Cultural Tour','Cultural Tour','Experience the rich culture and traditions along the Mekong River.',5,15,85.00,'Mekong River','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1000&q=80',_binary '\0',_binary '','2025-10-13 09:13:51','2025-10-13 09:13:51'),(5,'Danube Photography Tour','Photography Tour','Capture stunning landscapes and wildlife photos on the Danube River.',6,12,95.00,'Danube River','https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&w=1000&q=80',_binary '\0',_binary '','2025-10-13 09:13:51','2025-10-13 09:13:51'),(6,'Amazon Wildlife Safari','Duraa','Explore the diverse wildlife of the Amazon River with our expert guides.',3,25,75.00,'Amazon River','https://images.unsplash.com/photo-1594736797933-d0e013c8c7e7?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80',_binary '',_binary '','2025-10-13 09:22:11','2025-10-13 09:22:11');
/*!40000 ALTER TABLE `trips` ENABLE KEYS */;
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
