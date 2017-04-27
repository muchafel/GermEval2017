package de.germeval2017.Evaluation.EvaluationUtil;

public class Category {
 protected long tp;
 protected long fp;
 protected long fn;
 protected long tn;

	Category(long tp, long fp, long fn, long tn) {
		this.tp = tp;
		this.fp = fp;
		this.fn = fn;
		this.tn = tn;
	}
}
