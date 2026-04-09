package ownStrategy.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ownStrategy.model.Favorites;

public interface FavoritesRepository extends MongoRepository<Favorites, String> {
}
