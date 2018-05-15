# Standardization Proc Tests

Tests are done using `data_std_test` as dataset and `licences` as vocabulary.

## Test1: fix a wrong word
Row 1, changed `licenza_label_1` with wrong word: `Lizenzu` instead of `Licenza`
Row 4 changed `licenza_label_3` with wrong word: `Communi` instead of `Commons`
Row 5 changed `licenza_code_2` with wrong word: `CA.1` instead of `C.1`

## Test2: resolve amb iguity with linked columns
Row 6, which copies Row 5 and change `licenza_label_2` with another value contained into the vocabulary, which should be ignored if linked standardized columns have been used correctly.
