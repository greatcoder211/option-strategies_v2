package ownStrategy.service;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import jdk.dynalink.linker.support.Lookup;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import ownStrategy.dto.TradeSummaryDTO;
import ownStrategy.logic.sPattern.OptionType;
import ownStrategy.model.TheWallet;
import ownStrategy.mongoDBdto.*;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.locks.Condition;

import static com.mongodb.client.model.Filters.and;

@Service
public class MongoDBService {
    private final MongoTemplate mongoTemplate;
    public MongoDBService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    public List<TheWallet> findComplexStrategies() {
        MatchOperation filterLegs = Aggregation.match(
                Criteria.where("legs.2").exists(true)
        );
        Aggregation pipeline = Aggregation.newAggregation(filterLegs);
        AggregationResults<TheWallet> results = mongoTemplate.aggregate(
                pipeline,
                "thewallet-nocomment",
                TheWallet.class
        );
        return results.getMappedResults();
    }
    public List<TheWallet> findLatestTradesByTicker(String tickerName) {
        // 1. Filtr (Match) - szukamy konkretnej spółki
        MatchOperation filterByTicker = Aggregation.match(
                Criteria.where("ticker").is(tickerName)
        );
// 2. NOWOŚĆ: Sortowanie (SortOperation)
        // Sort.by - metoda fabryczna określająca kierunek i pole
        SortOperation sortByDate = Aggregation.sort(
                Sort.by(Sort.Direction.DESC, "date")
        );
// 3. Składanie rurociągu - KOLEJNOŚĆ MA ZNACZENIE
        // Najpierw filtrujemy (żeby nie sortować całej bazy!), potem sortujemy
        Aggregation pipeline = Aggregation.newAggregation(filterByTicker, sortByDate);
// 4. Wykonanie
        return mongoTemplate.aggregate(pipeline, "thewallet-nocomment", TheWallet.class)
                .getMappedResults();
    }
    public List<TheWallet> findTopThreeRecentStrategies() {
        // 1. Sortujemy od najnowszych
        SortOperation sortByDate = Aggregation.sort(
                Sort.by(Sort.Direction.DESC, "date")
        );
// 2. NOWOŚĆ: Limit (LimitOperation)
        // Pobiera tylko określoną liczbę dokumentów z góry listy
        LimitOperation limitToThree = Aggregation.limit(3);
// 3. Budowa rurociągu
        // Najpierw musimy posortować, żeby wiedzieć, które są "pierwsze"
        Aggregation pipeline = Aggregation.newAggregation(sortByDate, limitToThree);
        return mongoTemplate.aggregate(pipeline, "thewallet-nocomment", TheWallet.class)
                .getMappedResults();
    }
    //
    public List<TheWallet> getTopRiskTrades(String targetUserId){
        MatchOperation operation = Aggregation.match(Criteria.where("userID").is(targetUserId));
        MatchOperation tesla = Aggregation.match(Criteria.where("ticker").is("AAPL"));
        SortOperation sorto = Aggregation.sort(Sort.by(Sort.Direction.DESC, "date"));
        LimitOperation limit = Aggregation.limit(2);
        Aggregation aggregation = Aggregation.newAggregation(operation, tesla, sorto, limit);
        AggregationResults<TheWallet> res = mongoTemplate.aggregate(aggregation, "thewallet-nocomment", TheWallet.class);
        return res.getMappedResults();
    }
    public List<TheWallet> train2(String ticker){
        MatchOperation operation = Aggregation.match(Criteria.where("ticker").is(ticker));;
        SortOperation sort = Aggregation.sort(Sort.by(Sort.Direction.DESC, "userID"));
        Aggregation agg =  Aggregation.newAggregation(operation, sort);
        AggregationResults<TheWallet> res = mongoTemplate.aggregate(agg, "thewallet-nocomment", TheWallet.class);
        return res.getMappedResults();
    }
//ballin zaden jimmy butler jade przez elimii plater
    public List<TheWallet> TSLAones(){
        MatchOperation ope = Aggregation.match(Criteria.where("ticker").is("TSLA"));
        ProjectionOperation project = Aggregation.project("ticker", "strategyName")
                .andExclude("_id");
        Aggregation agg = Aggregation.newAggregation(ope, project);
        AggregationResults<TheWallet> res = mongoTemplate.aggregate(agg, "thewallet-nocomment", TheWallet.class);
        return res.getMappedResults();
    }
    public List<TradeSummaryDTO> train4(String userID){
                MatchOperation op = Aggregation.match(Criteria.where("userID").is(userID));
                ProjectionOperation genau = Aggregation.project()
                        .and(ArrayOperators.Size.lengthOfArray("legs"))
                        .as("numberOfLegs?");
        Aggregation agg = Aggregation.newAggregation(op, genau);
        AggregationResults<TradeSummaryDTO> res =  mongoTemplate.aggregate(agg, "thewallet-nocomment", TradeSummaryDTO.class);
        return res.getMappedResults();
    }
    public List<TradeSummaryDTO> zad1(){
//                .and(ArrayOperators.Size.lengthOfArray("legs"))
//                .as("numberOfLegs?");
        MatchOperation match = Aggregation.match(Criteria.where("ticker").is("TSLA.TRT"));
//        MatchOperation ope = Aggregation.match(Criteria.where("numberOfLegs").is(4));
        SortOperation sort = Aggregation.sort(Sort.by(Sort.Direction.DESC, "date"));
        ProjectionOperation fields = Aggregation.project("ticker")
                .and("strategyName").as("name")
                .and("date").as("date");
        Aggregation agg = Aggregation.newAggregation(match, sort, fields);
        AggregationResults<TradeSummaryDTO> res =  mongoTemplate.aggregate(agg, "thewallet-nocomment", TradeSummaryDTO.class);
        return res.getMappedResults();
    }
    public List<Zad2dto> zad2(){
        MatchOperation match = Aggregation.match(Criteria.where("type").is("CALL"));
        ProjectionOperation project = Aggregation.project("type")
                .and("strategyName").as("name")
                .andExclude("_id");
        Aggregation agg = Aggregation.newAggregation(project, match);
        AggregationResults<Zad2dto> results = mongoTemplate.aggregate(agg, "thewallet-nocomment", Zad2dto.class);
        return results.getMappedResults();
    }
    public double zad3(){
        UnwindOperation unwind = Aggregation.unwind("legs");
        MatchOperation match = Aggregation.match(Criteria.where("ticker").is("TSLA.TRT"));
        GroupOperation group = Aggregation.group("ticker")
                .sum("legs.strikePrice").as("totalPrice");
        Aggregation agg = Aggregation.newAggregation(unwind, match, group);
        AggregationResults<org.bson.Document> res = mongoTemplate.aggregate(agg, "thewallet-nocomment", org.bson.Document.class);
        return res.getUniqueMappedResult().get("totalPrice", Double.class);
    }

    public List<Zad4dto> zad4(){
        UnwindOperation unwind = Aggregation.unwind("legs");
        //to- tak samo jak zadanie 2- nie zadziała, potrzeba by na początku chyba zrobić addfieldsoperation i przekonwertować typy
        GroupOperation group = Aggregation.group("legs.belfort")
                .count().as("counterWOOOW");
        Aggregation agg = Aggregation.newAggregation(unwind, group);
        AggregationResults<Zad4dto> results = mongoTemplate.aggregate(agg, "thewallet-nocomment", Zad4dto.class);
        return results.getMappedResults();
    }

    public List<Zad5dto> zad5(){
        AddFieldsOperation convertID = Aggregation.addFields()
                .addFieldWithValue("userID", ConvertOperators.ToObjectId.toObjectId("$userID"))
                .build();
        LookupOperation lookup = LookupOperation.newLookup()
                .from("users")
                .localField("userID")
                .foreignField("_id")
                .as("userData");
        //userName jako pole klasy Zad5dto, 'username' jako pole klasy User
        UnwindOperation unwind = Aggregation.unwind("userData");
        MatchOperation match = Aggregation.match(Criteria.where("username").is("AntekFra35"));
        ProjectionOperation project = Aggregation.project("userName", "ticker");
        Aggregation agg = Aggregation.newAggregation(project, lookup, unwind, match, lookup);
        AggregationResults<Zad5dto> res = mongoTemplate.aggregate(agg, "thewallet-nocomment", Zad5dto.class);
        return res.getMappedResults();
    }

    public List<Integer> zad6(){
        AddFieldsOperation convertID = Aggregation.addFields()
                .addFieldWithValue("userID", ConvertOperators.ToObjectId.toObjectId("$userID"))
                .build();
        LookupOperation lookup = LookupOperation.newLookup()
                .from("users")
                .localField("userID")
                .foreignField("_id")
                .as("userData");
        GroupOperation group = Aggregation.group("username")
                .count().as("strategyCounter");
        SortOperation sort = Aggregation.sort(Sort.by(Sort.Direction.DESC, "strategyCounter"));
        Aggregation agg = Aggregation.newAggregation(convertID, lookup,group, sort);
        AggregationResults<Integer> res = mongoTemplate.aggregate(agg, "thewallet-nocomment", Integer.class);
        return res.getMappedResults();
    }

    public List<Zad7dto> zad7(){
        ProjectionOperation project = Aggregation.project()
                        .and(StringOperators.Concat.valueOf("ticker")
                        .concat("[")
                        .concatValueOf("type")
                        .concat("]"))
                        .as("fullSymbol")
                        .and("strategyName").as("name")
                        .andExclude("_id");
        Aggregation agg = Aggregation.newAggregation(project);
        AggregationResults<Zad7dto> results = mongoTemplate.aggregate(agg, "thewallet-nocomment", Zad7dto.class);
        return results.getMappedResults();
    }

    public List<ConcatDTO> concat2(){
        ProjectionOperation project = Aggregation.project()
                .and(StringOperators.Concat.valueOf("STRATEGY: ")
                        .concatValueOf("strategyName")
                        .concat(" | ASSET: ")
                        .concatValueOf("ticker"))
                .as("tradeSummary")
                .and("type").as("type")
                .andExclude("_id");
        Aggregation agg = Aggregation.newAggregation(project);
        AggregationResults<ConcatDTO> results = mongoTemplate.aggregate(agg, "thewallet-nocomment", ConcatDTO.class);
        return results.getMappedResults();
    }

    public List<Train1DTO> train1(){
        MatchOperation match = Aggregation.match(Criteria.where("ticker").is("TSLA.TRT"));
        UnwindOperation unwind = Aggregation.unwind("legs");
        GroupOperation group = Aggregation.group("legs.belfort")
                .count().as("counter");
        Aggregation agg = Aggregation.newAggregation(match, unwind, group);
        AggregationResults<Train1DTO> results = mongoTemplate.aggregate(agg, "thewallet-nocomment",  Train1DTO.class);
        return results.getMappedResults();
    }

    public List<org.bson.Document> train2(){
        AddFieldsOperation convertID = Aggregation.addFields()
                .addFieldWithValue("userID", ConvertOperators.ToObjectId.toObjectId("$userID"))
                .build();
        LookupOperation lookup = LookupOperation.newLookup()
                .from("users")
                .localField("userID")
                .foreignField("_id")
                .as("userData");
        UnwindOperation unwind = Aggregation.unwind("userData");
        MatchOperation match = Aggregation.match(Criteria.where("userData.username").is("AntekFra35"));
        UnwindOperation unwind2 = Aggregation.unwind("legs");
        GroupOperation group = Aggregation.group()
                .avg("legs.strikePrice").as("avgstrike");
        Aggregation agg = Aggregation.newAggregation(convertID, lookup, unwind, match, unwind2, group);
        AggregationResults<org.bson.Document> res = mongoTemplate.aggregate(agg, "thewallet-nocomment", org.bson.Document.class);
        return res.getMappedResults();
    }

    public List<TrainCDTO> whatsoever(){

        AddFieldsOperation addfields = Aggregation.addFields()
                .addFieldWithValue("totalLegs", ArrayOperators.Size.lengthOfArray("legs"))
                .addFieldWithValue("userID", ConvertOperators.ToObjectId.toObjectId("$userID"))
                .build();

        MatchOperation match = Aggregation.match(Criteria.where("totalLegs").gt(2));
        LookupOperation lookup = LookupOperation.newLookup()
                .from("users")
                .localField("userID")
                .foreignField("_id")
                .as("userData");
        UnwindOperation unwind = Aggregation.unwind("userData");
        ProjectionOperation project = Aggregation.project()
                .and("totalLegs").as("totalLegs")
                .and("ticker").as("tick")
                .and("strategyName").as("name")
                .and("userData.username").as("nickname")
                .andExclude("_id");

        Aggregation agg = Aggregation.newAggregation(addfields, match, lookup, unwind, project);
        AggregationResults<TrainCDTO> res = mongoTemplate.aggregate(agg, "thewallet-nocomment", TrainCDTO.class);
        return res.getMappedResults();
    }

    public List<FinalDTO> finalBoss(){
        AddFieldsOperation addFields = Aggregation.addFields()
                .addFieldWithValue("totalLegs", ArrayOperators.Size.lengthOfArray("legs"))
                .addFieldWithValue("positionSize", ConditionalOperators
                        .when(ComparisonOperators.Gt.valueOf("totalLegs").greaterThanValue(2))
//wiem, ze to nie ma sensu bo single odnosi się do liczby 1, ale pomin to, chodzi o naukę, nie będę na siłę teraz wymyslal czegoś poza-kodowego
                        .then("MULTIPLE")
                        .otherwise("SINGLE"))
                .build();
        ProjectionOperation project = Aggregation.project()
                .and("positionSize").as("complexity")
                .andExclude("_id");
        GroupOperation group = Aggregation.group("positionSize")
                .count().as("counter");
        Aggregation agg = Aggregation.newAggregation(addFields, project, group);
        AggregationResults<FinalDTO> res =  mongoTemplate.aggregate(agg, "thewallet-nocomment", FinalDTO.class);
        return res.getMappedResults();
    }

    //wszystkie strategie rentiera123
    public long countAllRentierLegs(String targetUserId) {
        // 1. Match - Filtrujemy dokumenty konkretnego usera
        MatchOperation filterUser = Aggregation.match(Criteria.where("userID").is(targetUserId));

        // 2. Unwind - Rozbijamy tablicę "legs" na pojedyncze rekordy
        UnwindOperation unwindLegs = Aggregation.unwind("legs");

        // 3. Group - Zwijamy wszystko do jednego wyniku i liczymy rekordy
        GroupOperation groupAll = Aggregation.group() // Pusty nawias = grupa "wszystko"
                .count().as("totalLegsCount");

        // 4. Składanie rurociągu
        Aggregation pipeline = Aggregation.newAggregation(filterUser, unwindLegs, groupAll);

        // 5. Wykonanie (używamy Document.class, żeby nie tworzyć specjalnego DTO dla jednej liczby)
        AggregationResults<org.bson.Document> results = mongoTemplate.aggregate(
                pipeline, "thewallet-nocomment", org.bson.Document.class
        );

        // Wyciągamy wynik - count w Mongo to Long
        return results.getUniqueMappedResult().get("totalLegsCount", Long.class);
    }

    public List<org.bson.Document> rentier() {

        AddFieldsOperation convertId = Aggregation.addFields()
                .addFieldWithValue("userID", ConvertOperators.ToObjectId.toObjectId("$userID"))
                .build();

        // 1. Join - łączymy portfel z kolekcją users
        LookupOperation lookup = LookupOperation.newLookup()
                .from("users")
                .localField("userID")
                .foreignField("_id")
                .as("userData");

        // 2. Unwind - zamieniamy tablicę userData na pojedynczy obiekt
        UnwindOperation unwind = Aggregation.unwind("userData");

        // 3. Match - teraz możemy szukać po polach z dołączonego użytkownika
        // Używamy notacji kropkowej: nazwa_z_lookup.pole_z_user
        MatchOperation filter = Aggregation.match(
                Criteria.where("userData.username").is("AntekFra35")
        );

        // 4. Składamy rurociąg
        Aggregation pipeline = Aggregation.newAggregation(convertId, lookup, unwind, filter);

        return mongoTemplate.aggregate(pipeline, "thewallet-nocomment", org.bson.Document.class)
                .getMappedResults();
    }

}
