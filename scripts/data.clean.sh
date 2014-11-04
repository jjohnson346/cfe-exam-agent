## This file is a bash script file for cleaning the data associated with the 
## 2014 Fraud Examiners Manual.  
##
## Overall approach for cleaning data for 2014 Fraud Examiners Manual
## 
## 1.  convert the file from pdf to txt.  That is, we convert the 
## 2014 Fraud Examiners Manual.pdf (downloaded from the CFE exam website)
## to 2014 Fraud Examiners Manual.txt by executing pdf converter software,
## iSkySoft pdf converter, version 3.0.0, on the pdf file, with 
## conversion format set to .txt.  This step is currently performed manually
## (outside of this script).  This script requires the existence of 
## the 2014 Fraud Examiners Manual.txt file prior to execution.
##
## 2.  Generate the 2014_zz_fem_toc.txt file.  This is accomplished by
## executing the toc phase compilers (TOCPhase1Compiler, TOCPhase2Compiler,
## TOCPhase3Compiler, ..., ToCPhase6Compiler), in turn.  For phases 1, 2, and
## 3, the toc phase compiler must be executed for each macro-section of the 
## manual, namely 
## 1. Main, 2. Financial Transactions and Fraud Schemes, 3. Law, 4. Investigation,
## 5. Fraud Prevention and Deterrence.  Thus, the execution sequence is as follows:
##
## 	TOCPhase1Compiler Main
## 	TOCPhase1Compiler Financial Transactions and Fraud Schemes
## 	TOCPhase1Compiler Law
## 	TOCPhase1Compiler Investigation
## 	TOCPhase1Compiler Fraud Prevention and Deterrence
##
## 	TOCPhase2Compiler Main
## 	TOCPhase2Compiler Financial Transactions and Fraud Schemes
## 	TOCPhase2Compiler Law
## 	TOCPhase2Compiler Investigation
## 	TOCPhase2Compiler Fraud Prevention and Deterrence
##
## 	TOCPhase3Compiler Main
## 	TOCPhase3Compiler Financial Transactions and Fraud Schemes
## 	TOCPhase3Compiler Law
## 	TOCPhase3Compiler Investigation
## 	TOCPhase3Compiler Fraud Prevention and Deterrence
##
##	TOCPhase4Compiler (requires output file for each section from phase 3)
## 	TOCPhase5Compiler (requires output file from phase 4)
## 	TOCPhase6Compiler (requires output file from phase 5)
## 
## Brief description of each phase:
## --------------------------------
## Phase 1:  Performs initial scrubbing of the toc sections of the fraud examiners
## 	manual, where a finite state transducer is used to 
## 	create a consistent set of records for the toc entries, with all breaks/interruptions/
## 	blank lines/page numbers, etc. removed.
##
## Phase 2:  Removes blanks mistakenly inserted during the pdf->text conversion
## 	process.  This involves executing a concatenation algorithm that checks non-word
## 	sequences against word lists after concatenating the non-word sequence with the word/
## 	words ahead of it (or, at least this is the case for TextCompilerFwdConcat (currently 
## 	being used in this phase; may use different algorithm for concatenating such as 
## 	MaxScore, instead)).  IMPORTANT NOTE:  This step is necessary because the component
## 	PDFExtractor generated blanks during the pdf->text conversion process.  This step
## 	may no longer be necessary now that we're using iSkySoft PDF Converter software.
## 
## Phase 3:  Aligns each line of the TOC so that each line is of the same length.
##
## Phase 4:  Merges the toc's of the macro-sections of the manual (main, financial
## 	transactions and fraud schemes, law, investigation, fraud prevention and deterrence).
##	Note that the main toc contains sections and subsections, and that each test area
## 	toc contains subsections and sub-subsections.  The sections, subsections, and sub-
##	subsections are merged into a hierarchical structure, in this step, and saved to
##	a file, 2014_fem_aggregate_toc_phase_4.txt.  Note the use of the label, "aggregate" -
##	which is used to denote that all of the toc files have been aggregated into a single
##	output file.
##
## Phase 5:  Aligns, once again, each line of the toc so that each line is the same length.
##	This is very similar to phase 3, but we must do this once again because of the lines
##	that have been inserted during the merging process of phase 4.  Question:  is this still
## 	necessary?  Or could we simply do one alignment phase after a merge, (and thus, skip 
##	phase 3?).
##
## Phase 6:  Inserts sub-sub-subsections into the toc (e.g., DEFINING INTERNAL CONTROL ..... 1.207)
## 	by finding those headings within the manual that are all capital letters, but are not 
## 	reflected in the current toc.  This adds granularity to the toc so that the cfeManual object
## 	constructed from it has a more granular hierarchy.
##
##
## 3.  Generate the 2014_fem_manual_text.txt file.
##	TODO:  add in some detail about the process for creating this file.
##
##
##
## 
## 
## input file:  2014 Fraud Examiners Manual.txt
## 
## output file(s): 2014_zz_fem_manual_text.txt
##                 2014_zz_fem_toc.txt
#java -cp ../bin financial.fraud.cfe.manual.TextExtractorPDF
