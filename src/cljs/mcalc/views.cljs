(ns mcalc.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [goog.string :as gstring]
            [goog.string.format]))

(def default-input
  {:fullPrice 600000
   :downPayment 50000
   :loanYears 30
   :loanRate 4
   :pmiRate 0.5
   :propertyTaxRate 0.943
   :incomeTaxRate 37.3
   :standardTaxDeduction 12700
   :hoa 400
   :homeInsurance 200
   :calcPeriods 60})

(def result (reagent/atom nil))

(defn calc-down-percent
  [input]
  (let [{:keys [downPayment fullPrice]} input]
    (gstring/format "%.2f" (* 100 (/ downPayment fullPrice)))))

(defn update-input
  [event atom key]
  (let [value (-> event .-target .-value)
        value (if (= value "") 0 (js/parseFloat value))]
    (js/console.log (type value))
    (swap! atom assoc key value)))

(defn input-form []
  (let [input (reagent/atom default-input)]
    (fn []
      [:div
       [:h3 "Put Your Numbers"]
       [:form {:on-submit
               #(do (.preventDefault %)
                    (reset! result (mcalc.core/calculate @input)))}
        [:div.line
         [:label "Full Price"]
         [:div.value
           [:input {:name "fullPrice"
                    :on-change #(update-input % input :fullPrice)
                    :default-value (:fullPrice @input)}]]]
        [:div.line
         [:label "Down Payment"]
         [:div.value
           [:input.short
            {:name "downPayment"
             :on-change #(update-input % input :downPayment)
             :default-value (:downPayment @input)}]
           [:span (calc-down-percent @input) "%"]]]
        [:div.line
         [:label "Loan Years"]
         [:div.value
           [:input {:name "loanYears"
                    :on-change #(update-input % input :loanYears)
                    :default-value (:loanYears @input)}]]]
        [:div.line
         [:label "Loan APR %"]
         [:div.value
           [:input {:name "loanRate"
                    :on-change #(update-input % input :loanRate)
                    :default-value (:loanRate @input)}]]]
        [:div.line
         [:label "PMI rate %"]
         [:div.value
           [:input {:name "pmiRate"
                    :on-change #(update-input % input :pmiRate)
                    :default-value (:pmiRate @input)}]]]
        [:div.line
         [:label "Property TAX rate %"]
         [:div.value
           [:input {:name "propertyTaxRate"
                    :on-change #(update-input % input :propertyTaxRate)
                    :default-value (:propertyTaxRate @input)}]]]
        [:div.line
         [:label "Income TAX rate %"]
         [:div.value
           [:input {:name "incomeTaxRate"
                    :on-change #(update-input % input :incomeTaxRate)
                    :default-value (:incomeTaxRate @input)}]]]
        [:div.line
         [:label "Standard TAX deduction $"]
         [:div.value
           [:input {:name "standardTaxDeduction"
                    :on-change #(update-input % input :standardTaxDeduction)
                    :default-value (:standardTaxDeduction @input)}]]]
        [:div.line
         [:label "HOA monthly $"]
         [:div.value
           [:input {:name "hoa"
                    :on-change #(update-input % input :hoa)
                    :default-value (:hoa @input)}]]]
        [:div.line
         [:label "Home Insurance monthly $"]
         [:div.value
           [:input {:name "homeInsurance"
                    :on-change #(update-input % input :homeInsurance)
                    :default-value (:homeInsurance @input)}]]]
        [:div.line
         [:label "Number of calculated months"]
         [:div.value
           [:input {:name "calcPeriods"
                    :on-change #(update-input % input :calcPeriods)
                    :default-value (:calcPeriods @input)}]]]
        [:div.button-row
         [:button "Calculate"]]]])))

(defn results-table []
  (fn []
    [:table
     [:thead
      [:tr
       [:th "Month #"]
       [:th "Principal"]
       [:th "Interest"]
       [:th "PMI"]
       [:th "TAX Deduction"]
       [:th "TAX Advantage"]
       [:th "Payment"]
       [:th "Adjusted Payment"]
       [:th "Lost Payment"]]]
     [:tbody
      (for [pm (:payments @result)]
        [:tr {:key (:monthN pm)}
         [:td (:monthN pm)]
         [:td (gstring/format "$%.2f" (:principal pm))]
         [:td (gstring/format "$%.2f" (:interest pm))]
         [:td (gstring/format "$%.2f" (:pmi pm))]
         [:td (gstring/format "$%.2f" (:taxDeduction pm))]
         [:td (gstring/format "$%.2f" (:taxAdvantage pm))]
         [:td (gstring/format "$%.2f" (:payment pm))]
         [:td (gstring/format "$%.2f" (:adjustedPayment pm))]
         [:td (gstring/format "$%.2f" (:wastedMoney pm))]])]]))

(defn main-panel []
  [:div.app
    [:h1 "How much money do I loose on a Mortgage"]
    [input-form]
    [results-table]])
