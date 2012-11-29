.PHONY: clean pdf/Report.pdf

PDFLATEX_FLAGS=-shell-escape -halt-on-error -interaction=nonstopmode

pdf/Report.pdf: latex/*
	cp latex/*.latex latex/*.bib tmp/
	cd tmp; \
		pdflatex $(PDFLATEX_FLAGS) Report.latex; \
		bibtex Report.aux; \
		pdflatex $(PDFLATEX_FLAGS) Report.latex; \
		pdflatex $(PDFLATEX_FLAGS) Report.latex;
	mv tmp/Report.pdf pdf/

clean:
	rm -f tmp/* pdf/*
