/*
SQLyog Ultimate v8.71 
MySQL - 5.5.51-log : Database - crayonbot
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

/*Table structure for table `customer` */

DROP TABLE IF EXISTS `customer`;

CREATE TABLE `customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `company_name` varchar(255) DEFAULT '',
  `created_date` datetime DEFAULT NULL,
  `status` int(11) DEFAULT '2',
  `is_admin` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

/*Data for the table `customer` */

LOCK TABLES `customer` WRITE;

insert  into `customer`(`id`,`name`,`password`,`phone`,`email`,`company_name`,`created_date`,`status`,`is_admin`) values (4,'Ha Nguyen','HASHEDhpOHPNj4otnHxZZHcYD4UeUl9Or1Wk9je0RctEKl40A=/HASHED','85911439','hanguyen@crayondata.com','Crayondata','2016-10-16 14:02:28',2,'');

UNLOCK TABLES;

/*Table structure for table `customer_app` */

DROP TABLE IF EXISTS `customer_app`;

CREATE TABLE `customer_app` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `customer_id` bigint(20) DEFAULT NULL,
  `application_name` varchar(255) DEFAULT NULL,
  `application_id` varchar(100) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `expired_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

/*Data for the table `customer_app` */

LOCK TABLES `customer_app` WRITE;

insert  into `customer_app`(`id`,`customer_id`,`application_name`,`application_id`,`created_date`,`expired_date`,`request_limited`) values (1,4,'Hawtbot','3518019034437350284','2016-10-16 14:02:28',NULL,-1);

UNLOCK TABLES;

/*Table structure for table `f_conversation` */

DROP TABLE IF EXISTS `f_conversation`;

CREATE TABLE `f_conversation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `question` text COLLATE utf8_unicode_ci,
  `answer` text COLLATE utf8_unicode_ci,
  `posted_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1531 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `f_conversation` */

/*Table structure for table `f_liked` */

DROP TABLE IF EXISTS `f_liked`;

CREATE TABLE `f_liked` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `conversation_id` bigint(20) DEFAULT NULL,
  `rest_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `f_liked` */

/*Table structure for table `f_user` */

DROP TABLE IF EXISTS `f_user`;

CREATE TABLE `f_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(25) COLLATE utf8_unicode_ci NOT NULL,
  `token` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `email` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `c_latitude` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `c_longitude` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `fb_id` varchar(100) COLLATE utf8_unicode_ci DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `f_user` */

ALTER TABLE customer_app ADD request_limited BIGINT(20) DEFAULT 1000;
ALTER TABLE customer_app ADD request_counted BIGINT(20) DEFAULT 0;
UPDATE customer_app SET request_limited = -1 WHERE application_id = "3518019034437350284"

-- Added on 03/03/2017
ALTER TABLE customer ADD is_system_admin BIT DEFAULT b'0';
ALTER TABLE customer ADD parent_id BIGINT DEFAULT 0;
ALTER TABLE customer ADD contract_start DATETIME;
ALTER TABLE customer ADD contract_end DATETIME;
ALTER TABLE customer ADD request_limited BIGINT DEFAULT 0;

insert  into `customer_app`(`id`,`customer_id`,`application_name`,`application_id`,`created_date`,`expired_date`, `request_limited`) values (1,4,'TestScriptApp','1248590550588513458','2016-10-16 14:02:28',NULL,-1);

CREATE TABLE `customer_query` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `application_id` VARCHAR(100) COLLATE utf8_general_ci DEFAULT NULL,
  `token` VARCHAR(255) COLLATE utf8_general_ci DEFAULT NULL,
  `queried_date` DATETIME DEFAULT NULL,
  `is_success` BIT(1) DEFAULT b'1' NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci

