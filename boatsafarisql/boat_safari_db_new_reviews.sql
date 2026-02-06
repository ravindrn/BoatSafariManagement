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
-- Table structure for table `new_reviews`
--

DROP TABLE IF EXISTS `new_reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `new_reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` text NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `customer_name` varchar(255) NOT NULL,
  `rating` int NOT NULL,
  `status` enum('APPROVED','PENDING','REJECTED') DEFAULT NULL,
  `trip_name` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `new_reviews`
--

LOCK TABLES `new_reviews` WRITE;
/*!40000 ALTER TABLE `new_reviews` DISABLE KEYS */;
INSERT INTO `new_reviews` VALUES (1,'The Amazon Wildlife Safari was absolutely incredible! Our guide was knowledgeable and we spotted so many exotic animals. Highly recommend this experience!','2025-10-13 15:55:29.134742','Sarah Johnson',5,'APPROVED','Amazon Wildlife Safari','2025-10-13 15:55:29.134742'),(2,'The sunset cruise on the Nile was magical. The colors, the scenery, and the comfortable boat made for a perfect evening. Will definitely book again!','2025-10-13 15:55:29.182665','Michael Brown',4,'APPROVED','Nile Sunset Cruise','2025-10-13 15:55:29.182665'),(3,'As a bird enthusiast, the Mississippi Bird Watching tour exceeded my expectations. We saw rare species I\'ve only read about in books. Fantastic!','2025-10-13 15:55:29.184664','Emma Davis',5,'APPROVED','Mississippi Bird Watching','2025-10-13 15:55:29.184664'),(4,'xvnadighWIFJAEPRGPidnia4k','2025-10-13 15:58:42.008892','duraa',5,'PENDING','Nile Sunset Cruise','2025-10-13 15:58:42.008892');
/*!40000 ALTER TABLE `new_reviews` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-16 22:28:23
