import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameCombinationsService {
    
    public List<ArrayList<Card>> getCombinations(Card playedCard, ArrayList<Card> table) {
        Map<String, Set<Set<Card>>> mapCombinations = new HashMap<>();
        this.initializeMapCombinations(mapCombinations);

        return this.createWinningCombinations(playedCard, table, mapCombinations);
    }

    private void initializeMapCombinations(Map<String, Set<Set<Card>>> mapCombinations) {
        mapCombinations.put("equals", new LinkedHashSet<>());
        mapCombinations.put("additions", new LinkedHashSet<>());
        mapCombinations.put("multiAdditions", new LinkedHashSet<>());
        mapCombinations.put("equalsCombined", new LinkedHashSet<>());
    }

    // merges all map combinations into one allCombinations with added played card
    private List<ArrayList<Card>> mergeMapCombinations(Card playedCard, Map<String, Set<Set<Card>>> mapCombinations) {
        List<ArrayList<Card>> mergedList = new ArrayList<>();
        // merge 
        for(String combo: mapCombinations.keySet()) {
            for(Set<Card> currCombination: mapCombinations.get(combo)) {
                mergedList.add(new ArrayList<>(currCombination));
            }
            
        }

        // add played card to all merged
        for(ArrayList<Card> combo: mergedList) {
            combo.add(playedCard);
        } 

        // sort merged
        mergedList.sort((a,b) -> {
            return b.size() - a.size();
        });


        return mergedList;

        // System.out.println("all combinations:");
        // System.out.println(allCombinations.toString());
    }

    private List<ArrayList<Card>> createWinningCombinations(Card playedCard, ArrayList<Card> currentTable, Map<String, Set<Set<Card>>> mapCombinations) {
        
        // equal combinations can use card symbol or value to check if table card is duplicate of played card
        // check for table card == played card
        for(int i = 0; i < currentTable.size(); i++) {
            if(currentTable.get(i).getValue() == playedCard.getValue()) {
                LinkedHashSet<Card> oneCardDuplicate = new LinkedHashSet<>();
                oneCardDuplicate.add(currentTable.get(i));
                mapCombinations.get("equals").add(oneCardDuplicate);
            }
        }

        // check two table cards == played card (player playes K => he should be able to take up to three K-s, two from table and played card)
        for(int i = 0; i < currentTable.size(); i++) {
            for(int j = 0; j < currentTable.size(); j++) {
                // skip same comparisons as values would be the same
                if(i == j) {
                    continue;
                }
                // find two same card values on table
                if(currentTable.get(i).getValue() == currentTable.get(j).getValue()) {
                    // check if two same values are equal to player card value
                    if(currentTable.get(i).getValue() == playedCard.getValue()) {
                        LinkedHashSet<Card> twoCardDuplicates = new LinkedHashSet<>();
                        twoCardDuplicates.add(currentTable.get(i));
                        twoCardDuplicates.add(currentTable.get(j));
                        mapCombinations.get("equals").add(twoCardDuplicates);
                    }
                }
            }
        }

        // check three table cards == played card (player playes K => he should be able to take up to four K-s, three from table and played card)
        int threeDuplicates = 0;
        int[] threeDuplicatesTableIndexes = new int[3];
        for(int i = 0; i < currentTable.size(); i++) {
            if(playedCard.getValue() == currentTable.get(i).getValue()) {
                // save index positions from found duplicates
                threeDuplicatesTableIndexes[threeDuplicates] = i;
                threeDuplicates++;
            }
        }

        if(threeDuplicates == 3) {
            LinkedHashSet<Card> threeCard = new LinkedHashSet<>();
            for(int i = 0; i < 3; i++) {
                threeCard.add(currentTable.get(threeDuplicatesTableIndexes[i]));
            }
            
            mapCombinations.get("equals").add(threeCard);
        }


        // generate combinations
        findAdditionCombinations(playedCard.getValue(), currentTable, mapCombinations);
        findMultipleAdditionCombinations(mapCombinations);
        findEqualsCombinedCombinations(mapCombinations);
        List<ArrayList<Card>> allCombinations = mergeMapCombinations(playedCard, mapCombinations);

        // System.out.println("EQUAL COMBINATIONS:");
        // System.out.println(mapCombinations.get("equals"));
        // System.out.println("ADDITION COMBINATIONS:");
        // System.out.println(mapCombinations.get("additions"));
        // System.out.println("MULTI ADDITION COMBINATIONS:");
        // System.out.println(mapCombinations.get("multiAdditions"));
        // System.out.println("EQUAL COMBINED COMBINATIONS:");
        // System.out.println(mapCombinations.get("equalsCombined"));
        // System.out.println("ALL COMBINATIONS:");
        // System.out.println(allCombinations);

        return allCombinations;

        

    }

    // generates all unique combinations where value of n cards == played card value
    private void findAdditionCombinations(int targetSum, ArrayList<Card> currentTable, Map<String, Set<Set<Card>>> mapCombinations) {
        additionCombinationsRecursion(0, new ArrayList<>(), 0, targetSum, currentTable, mapCombinations);
    }

    private void additionCombinationsRecursion(int currentI, ArrayList<Card> currentCombination, int totalSum, int targetSum, ArrayList<Card> currentTable, Map<String, Set<Set<Card>>> mapCombinations) {

        if(totalSum == targetSum) {
            if(currentCombination.size() > 1) {
                mapCombinations.get("additions").add(new LinkedHashSet<>(currentCombination));
            }
            return;
        }

        if(currentI >= currentTable.size() || totalSum > targetSum) {
            return;
        }
        
        Card card = currentTable.get(currentI);
        currentCombination.add(card);

        
        // cards are always one value unless the card is ACE which can be both 1 or 11
        for(int value: card.getPossibleValues()) {
            if(totalSum + value <= targetSum) {
                additionCombinationsRecursion(currentI + 1, currentCombination, totalSum + value, targetSum, currentTable, mapCombinations);
            }
        }

        currentCombination.remove(currentCombination.size() - 1);
        additionCombinationsRecursion(currentI + 1, currentCombination, totalSum, targetSum, currentTable, mapCombinations);
    }

    // generates all combinations of unique addition combinations: 
    // addition combinations: [5-c, 3-c], [5-c, 3-d], [5-d, 3-d], [5-d, 3-c] => [5-c, 3-c, 5-d, 3-d] + played card
    private void findMultipleAdditionCombinations(Map<String, Set<Set<Card>>> mapCombinations) {
        int n = mapCombinations.get("additions").size();
        int subsetCount = 1 << n; // 2^n subsets

         ArrayList<Set<Card>> additionsList = new ArrayList<>(mapCombinations.get("additions"));

        for (int mask = 1; mask < subsetCount; mask++) {
            ArrayList<Card> merged = new ArrayList<>();
            boolean valid = true;

            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {

                    // check if this group overlaps with merged
                    for (Card c : additionsList.get(i)) {
                        if (merged.contains(c)) {
                            valid = false;
                            break;
                        }
                    }

                    if (!valid) break;

                    merged.addAll(additionsList.get(i));
                }
            }

            if (valid && Integer.bitCount(mask) > 1) {
                mapCombinations.get("multiAdditions").add(new LinkedHashSet<>(merged));
            }
        }

    }

    // merges equals + additions and equals + multiadditions
    private void findEqualsCombinedCombinations(Map<String, Set<Set<Card>>> mapCombinations) {
        Set<Set<Card>> equalsCopy = new LinkedHashSet<>(mapCombinations.get("equals"));
        Set<Set<Card>> additionsCopy = new LinkedHashSet<>(mapCombinations.get("additions"));
        Set<Set<Card>> multiAdditionsCopy = new LinkedHashSet<>(mapCombinations.get("multiAdditions"));

        for(Set<Card> equals: equalsCopy) {
            for(Set<Card> addition: additionsCopy) {
                // check for ACE overlap where one SAME ace can be in equals and additions
                if(isAceOverlapping(equals, addition)) {
                    continue;
                }
                Set<Card> additionEquals = new LinkedHashSet<>(addition);
                additionEquals.addAll(equals);
                mapCombinations.get("equalsCombined").add(additionEquals);
            }

            for(Set<Card> multiAddition: multiAdditionsCopy) {
                // check for ACE overlap where one SAME ace can be in equals and multi additions
                if(isAceOverlapping(equals, multiAddition)) {
                    continue;
                }
                Set<Card> multiAdditionEquals = new LinkedHashSet<>(multiAddition);
                multiAdditionEquals.addAll(equals);
                mapCombinations.get("equalsCombined").add(multiAdditionEquals);
            }
        }

    }

    // checks and prevents if there is overlap with 1 and 11 rule where ACE can have two values 1 or 11, example:
    // equals = [A-d] additions = [7-d, A-d, 3-c], now in findEqualsCombinedCombinations() there will be overlap
    // findEqualsCombinedCombinations() merges [A-d] with [7-d, A-d, 3-c] => Set prevents [A-d, 7-d, A-d, 3-c] so the combination ends up [7-d, A-d, 3-c] which is SAME as valid addition combination
    private boolean isAceOverlapping(Set<Card> equals, Set<Card> additionsMultiAdditions) {
        boolean overlap = false;
        for(Card c: equals) {
            if(additionsMultiAdditions.contains(c)) {
                overlap = true;
                break;
            }
        }
        
        return overlap;
    }
}
