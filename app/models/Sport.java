package models;

import javax.persistence.Entity;

import play.db.jpa.Model;
import sports.ISport;

@Entity
public abstract class Sport extends Model implements ISport {
	
}
