package models;

import java.util.List;

import javax.persistence.Entity;

import play.db.jpa.Model;
import util.ISport;

@Entity
public abstract class Sport extends Model implements ISport {}
